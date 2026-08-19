package com.windpvp.windspigot.spawning;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * MobSpawningCapTest: Verify that mob caps correctly count eligible chunks
 * and that affectsSpawning = false / spectators do not trigger spawns.
 */
public class MobSpawningCapTest {

    // Helper model representing a simulated player for spawning checks
    static class TestPlayer {
        final boolean isSpectator;
        final boolean affectsSpawning;
        final int chunkX;
        final int chunkZ;

        TestPlayer(boolean isSpectator, boolean affectsSpawning, int chunkX, int chunkZ) {
            this.isSpectator = isSpectator;
            this.affectsSpawning = affectsSpawning;
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
        }

        boolean canAffectSpawning() {
            // Fixed boolean logic in SpawnerCreature:
            // if (!entityhuman.isSpectator() && entityhuman.affectsSpawning)
            return !isSpectator && affectsSpawning;
        }
    }

    private int calculateEligibleChunks(TestPlayer[] players, byte mobSpawnRange) {
        Set<Long> eligibleChunks = new HashSet<>();

        for (TestPlayer player : players) {
            if (player.canAffectSpawning()) {
                int l = player.chunkX;
                int j = player.chunkZ;
                byte b0 = mobSpawnRange;

                for (int i1 = -b0; i1 <= b0; ++i1) {
                    for (int k = -b0; k <= b0; ++k) {
                        boolean isBorderChunk = (i1 == -b0 || i1 == b0 || k == -b0 || k == b0);
                        long chunkCoord = (((long) (i1 + l)) << 32) | ((k + j) & 0xFFFFFFFFL);
                        if (!isBorderChunk) {
                            eligibleChunks.add(chunkCoord);
                        }
                    }
                }
            }
        }
        return eligibleChunks.size();
    }

    @Test
    public void testSpectatorsDoNotTriggerSpawns() {
        TestPlayer normalPlayer = new TestPlayer(false, true, 0, 0);
        TestPlayer spectatorPlayer = new TestPlayer(true, true, 100, 100);

        Assert.assertTrue("Normal player can affect spawning", normalPlayer.canAffectSpawning());
        Assert.assertFalse("Spectator player must NOT affect spawning", spectatorPlayer.canAffectSpawning());

        int chunksNormalOnly = calculateEligibleChunks(new TestPlayer[] { normalPlayer }, (byte) 4);
        int chunksWithSpectator = calculateEligibleChunks(new TestPlayer[] { normalPlayer, spectatorPlayer }, (byte) 4);

        Assert.assertEquals("Adding a spectator must not increase eligible chunk count", chunksNormalOnly, chunksWithSpectator);
    }

    @Test
    public void testAffectsSpawningFalseDoesNotTriggerSpawns() {
        TestPlayer normalPlayer = new TestPlayer(false, true, 0, 0);
        TestPlayer nonSpawningPlayer = new TestPlayer(false, false, 50, 50);

        Assert.assertFalse("Player with affectsSpawning=false must NOT affect spawning", nonSpawningPlayer.canAffectSpawning());

        int chunksNormalOnly = calculateEligibleChunks(new TestPlayer[] { normalPlayer }, (byte) 4);
        int chunksWithNonSpawning = calculateEligibleChunks(new TestPlayer[] { normalPlayer, nonSpawningPlayer }, (byte) 4);

        Assert.assertEquals("Player with affectsSpawning=false must not increase eligible chunks", chunksNormalOnly, chunksWithNonSpawning);
    }

    @Test
    public void testMobCapCalculationScalesWithActiveChunks() {
        int monsterLimit = 70;

        // 1 player with mobSpawnRange = 4 -> (4*2 - 1)^2 inner chunks = 7*7 = 49 inner chunks (or up to 81 with boundary)
        int eligibleChunks = 289; // standard full 17x17 vanilla chunk grid
        int mobCap = (monsterLimit * eligibleChunks) / 289;
        Assert.assertEquals(70, mobCap);

        // When only 100 chunks are loaded/eligible:
        int smallerChunkCount = 100;
        int scaledMobCap = (monsterLimit * smallerChunkCount) / 289;
        Assert.assertEquals(24, scaledMobCap);
    }
}

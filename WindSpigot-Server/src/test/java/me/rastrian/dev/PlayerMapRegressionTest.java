package me.rastrian.dev;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class PlayerMapRegressionTest {

    private static final int CHUNK_BITS = 5;

    private static long xzToKey(long x, long z) {
        return (x << 32) + z - Integer.MIN_VALUE;
    }

    static class MockPlayer {
        final String name;
        double locX;
        double locZ;
        int playerMapX;
        int playerMapZ;

        MockPlayer(String name, double locX, double locZ) {
            this.name = name;
            this.locX = locX;
            this.locZ = locZ;
        }

        double distanceSq(double x, double z) {
            double dx = this.locX - x;
            double dz = this.locZ - z;
            return dx * dx + dz * dz;
        }
    }

    @Test
    public void testPlayerMapAddMoveRemove() {
        Long2ObjectMap<List<MockPlayer>> map = new Long2ObjectOpenHashMap<>();
        MockPlayer p1 = new MockPlayer("Steve", 10.0, 10.0);

        int chunkX = (int) Math.floor(p1.locX) >> CHUNK_BITS;
        int chunkZ = (int) Math.floor(p1.locZ) >> CHUNK_BITS;
        long key = xzToKey(chunkX, chunkZ);

        // Add
        List<MockPlayer> list = map.get(key);
        if (list == null) {
            list = new ArrayList<>();
            map.put(key, list);
        }
        list.add(p1);
        p1.playerMapX = chunkX;
        p1.playerMapZ = chunkZ;

        Assert.assertTrue(map.containsKey(key));
        Assert.assertEquals(1, map.get(key).size());

        // Move to new chunk
        p1.locX = 500.0;
        p1.locZ = 500.0;
        int newChunkX = (int) Math.floor(p1.locX) >> CHUNK_BITS;
        int newChunkZ = (int) Math.floor(p1.locZ) >> CHUNK_BITS;
        long newKey = xzToKey(newChunkX, newChunkZ);

        // Remove from old
        List<MockPlayer> oldList = map.get(key);
        oldList.remove(p1);
        if (oldList.isEmpty()) {
            map.remove(key);
        }

        // Add to new
        List<MockPlayer> newList = map.get(newKey);
        if (newList == null) {
            newList = new ArrayList<>();
            map.put(newKey, newList);
        }
        newList.add(p1);
        p1.playerMapX = newChunkX;
        p1.playerMapZ = newChunkZ;

        Assert.assertFalse("Old chunk key must be cleaned up", map.containsKey(key));
        Assert.assertTrue("New chunk key must contain player", map.containsKey(newKey));
        Assert.assertEquals(1, map.get(newKey).size());
    }
}

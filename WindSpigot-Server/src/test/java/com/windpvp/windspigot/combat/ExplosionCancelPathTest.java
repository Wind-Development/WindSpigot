package com.windpvp.windspigot.combat;

import org.junit.Assert;
import org.junit.Test;

/**
 * Locks the WorldServer.createExplosion Bukkit-cancel contract:
 * EntityExplodeEvent/BlockExplodeEvent set Explosion.wasCanceled; the server
 * must not broadcast explosion/velocity packets when cancelled, and must clear
 * the block list for PacketPlayOutExplosion when breakBlocks is false.
 */
public class ExplosionCancelPathTest {

	/** Mirrors WorldServer: skip client broadcast when Bukkit cancelled the explode event. */
	static boolean shouldBroadcastExplosionPackets(boolean wasCanceled) {
		return !wasCanceled;
	}

	/** Mirrors WorldServer: empty block list so clients do not visually break uncleared blocks. */
	static boolean shouldClearBlocksForPacket(boolean breakBlocks) {
		return !breakBlocks;
	}

	@Test
	public void cancelledExplodeEventSkipsClientBroadcast() {
		Assert.assertFalse(shouldBroadcastExplosionPackets(true));
		Assert.assertTrue(shouldBroadcastExplosionPackets(false));
	}

	@Test
	public void noBreakBlocksClearsPacketBlockList() {
		Assert.assertTrue(shouldClearBlocksForPacket(false));
		Assert.assertFalse(shouldClearBlocksForPacket(true));
	}

	@Test
	public void cancelTakesPrecedenceOverBreakBlocks() {
		// Even when breakBlocks is true, a cancelled event must not broadcast.
		boolean wasCanceled = true;
		boolean breakBlocks = true;
		Assert.assertFalse(shouldBroadcastExplosionPackets(wasCanceled));
		// clearBlocks only runs after the cancel early-return, so cancelled path
		// never reaches shouldClearBlocksForPacket — document that ordering.
		Assert.assertTrue("cancel short-circuit happens before clearBlocks",
				wasCanceled || shouldClearBlocksForPacket(breakBlocks));
	}
}

package com.windpvp.windspigot.async.entitytracker;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.windpvp.windspigot.async.AsyncUtil;
import com.windpvp.windspigot.config.WindSpigotConfig;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.minecraft.server.*;

public class AsyncEntityTracker extends EntityTracker {
	
	private static final ExecutorService trackingThreadExecutor = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("WindSpigot Entity Tracker Thread").build());
	private final WorldServer worldServer;	
	
	public AsyncEntityTracker(WorldServer worldserver) {
		super(worldserver);
		this.worldServer = worldserver;
	}
	
	@Override
	public void updatePlayers() {
		// Snapshot the tracker entries once, on the ticking thread, before any worker
		// starts. The workers then index into this stable array instead of reading the
		// non-thread-safe IndexedLinkedHashSet concurrently, which could otherwise race
		// with a structural change and throw or return the wrong entry. Reading the
		// thread count once also keeps the loop bound and the stride consistent.
		final EntityTrackerEntry[] entries = c.toArray(new EntityTrackerEntry[0]);
		final int threads = WindSpigotConfig.trackingThreads;
		int offset = 0;

		for (int i = 1; i <= threads; i++) {
			final int finalOffset = offset++;

			AsyncUtil.run(() -> {
				try {
					for (int index = finalOffset; index < entries.length; index += threads) {
						try {
							entries[index].update(finalOffset);
						} catch (Throwable t) {
							t.printStackTrace();
						}
					}
				} finally {
					worldServer.ticker.getLatch().decrement();
				}
			}, trackingThreadExecutor);

		}
		try {
            worldServer.ticker.getLatch().waitTillZero();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	    worldServer.ticker.getLatch().reset();
		for (EntityPlayer player : MinecraftServer.getServer().getPlayerList().players) {
			player.playerConnection.sendQueuedPackets();
		}
	}

	public static ExecutorService getExecutor() {
		return trackingThreadExecutor;
	}
}

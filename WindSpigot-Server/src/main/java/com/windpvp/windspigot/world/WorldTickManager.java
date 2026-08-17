package com.windpvp.windspigot.world;

import java.util.List;
import com.google.common.collect.Lists;
import com.windpvp.windspigot.config.WindSpigotConfig;

import co.aikar.timings.SpigotTimings;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldServer;

public class WorldTickManager {

	// List of cached world tickers
	private final List<WorldTicker> worldTickers = Lists.newArrayList();

	// Instance
	private static WorldTickManager worldTickerManagerInstance;
		
	// Initializes the world ticker manager
	public WorldTickManager() {
		worldTickerManagerInstance = this;

	}

    private void cacheWorlds() {
        // Always sync with the live worlds list
        worldTickers.clear();

        // Create world tickers
        for (WorldServer world : MinecraftServer.getServer().worlds) {
            if (world.ticker == null) {
                world.ticker = new WorldTicker(world);
            }
            worldTickers.add(world.ticker);
        }
    }

	// Ticks all worlds
	public void tick() {
		tickWorlds();
	}
	
	private void tickWorlds() {
        // Move BukkitScheduler stuff here so async entity tracking does not interfere
        SpigotTimings.bukkitSchedulerTimer.startTiming(); // Spigot
        // CraftBukkit start
        MinecraftServer.getServer().server.getScheduler().mainThreadHeartbeat(MinecraftServer.getServer().at());
        SpigotTimings.bukkitSchedulerTimer.stopTiming(); // Spigot

        // Cache world tick runnables if not cached already
        cacheWorlds();
        
        for (WorldTicker ticker : this.worldTickers) {
            ticker.run();
        }
	}

	/*
	 * The world ticker manager instance
	 */
	public static WorldTickManager getInstance() {
		return worldTickerManagerInstance;
	}
}

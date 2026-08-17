package com.windpvp.windspigot.world;

import co.aikar.timings.SpigotTimings;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldServer;

public class WorldTickManager {

	// Instance
	private static WorldTickManager worldTickerManagerInstance;
		
	// Initializes the world ticker manager
	public WorldTickManager() {
		worldTickerManagerInstance = this;

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
        
	for (int i = 0; i < MinecraftServer.getServer().worlds.size(); ++i) {
            WorldServer world = MinecraftServer.getServer().worlds.get(i);
			if (world.ticker == null) {
            	world.ticker = new WorldTicker(world);
			}
			world.ticker.run();
        }
	}

	/*
	 * The world ticker manager instance
	 */
	public static WorldTickManager getInstance() {
		return worldTickerManagerInstance;
	}
}

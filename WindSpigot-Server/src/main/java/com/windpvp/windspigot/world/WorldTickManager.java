package com.windpvp.windspigot.world;

import java.util.List;
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
        
        // WindSpigot star - dynamic world ticking and prevent world unload memory leaks
        List<WorldServer> worlds = MinecraftServer.getServer().worlds;
        for (int i = 0; i < worlds.size(); i++) {
            WorldServer world = worlds.get(i);
            if (world == null) {
                continue;
            }
            WorldTicker ticker = world.ticker;
            if (ticker == null) {
                ticker = new WorldTicker(world);
                world.ticker = ticker;
            }
            ticker.run();
        }
        // WindSpigot end
	}

	/*
	 * The world ticker manager instance
	 */
	public static WorldTickManager getInstance() {
		return worldTickerManagerInstance;
	}
}

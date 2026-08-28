package com.windpvp.windspigot.world;

import com.windpvp.windspigot.async.ResettableLatch;
import com.windpvp.windspigot.async.entitytracker.AsyncEntityTracker;
import com.windpvp.windspigot.config.WindSpigotConfig;

import net.minecraft.server.CrashReport;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NetworkManager;
import net.minecraft.server.PlayerConnection;
import net.minecraft.server.ReportedException;
import net.minecraft.server.WorldServer;

public class WorldTicker implements Runnable {

	public final WorldServer worldserver;
	private final ResettableLatch latch = new ResettableLatch(WindSpigotConfig.trackingThreads);
	private final Runnable cachedUpdateTrackerTask;
	protected volatile boolean hasTracked = false;
	
	public WorldTicker(WorldServer worldServer) {
		this.worldserver = worldServer;
		cachedUpdateTrackerTask = () -> {
			hasTracked = true;
			worldserver.getTracker().updatePlayers();
		};
	}

	// This is mostly copied code from world ticking
	@Override
	public void run() {
		// this.methodProfiler.a(worldserver.getWorldData().getName());
		// this.methodProfiler.a("tick");
		CrashReport crashreport;

		try {
			worldserver.timings.doTick.startTiming(); // Spigot
			worldserver.doTick();
			worldserver.timings.doTick.stopTiming(); // Spigot
		} catch (Throwable throwable) {
			// Spigot Start
			try {
				crashreport = CrashReport.a(throwable, "Exception ticking world");
			} catch (Throwable t) {
				throw new RuntimeException("Error generating crash report", t);
			}
			// Spigot End
			worldserver.a(crashreport);
			throw new ReportedException(crashreport);
		}

		try {
			worldserver.timings.tickEntities.startTiming(); // Spigot
			worldserver.tickEntities();
			worldserver.timings.tickEntities.stopTiming(); // Spigot
		} catch (Throwable throwable1) {
			// Spigot Start
			try {
				crashreport = CrashReport.a(throwable1, "Exception ticking world entities");
			} catch (Throwable t) {
				throw new RuntimeException("Error generating crash report", t);
			}
			// Spigot End
			worldserver.a(crashreport);
			throw new ReportedException(crashreport);
		}

        worldserver.timings.tracker.startTiming(); // Spigot
		// this.methodProfiler.b();
		// this.methodProfiler.a("tracker");
		if (MinecraftServer.getServer().getPlayerList().getPlayerCount() != 0) // Tuinity
		{
			worldserver.getTracker().updatePlayers();
		}

		worldserver.timings.tracker.stopTiming(); // Spigot
		// this.methodProfiler.b();
		// this.methodProfiler.b();
		worldserver.explosionDensityCache.clear(); // Paper - Optimize explosions
	}
	
	public ResettableLatch getLatch() {
		return latch;
	}

}

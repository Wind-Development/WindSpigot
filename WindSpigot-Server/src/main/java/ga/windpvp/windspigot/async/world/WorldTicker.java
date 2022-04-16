package ga.windpvp.windspigot.async.world;

import java.util.List;

import ga.windpvp.windspigot.WorldTickerManager;
import net.minecraft.server.CrashReport;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NetworkManager;
import net.minecraft.server.PlayerConnection;
import net.minecraft.server.ReportedException;
import net.minecraft.server.WorldServer;

public class WorldTicker implements Runnable {

	public final WorldServer worldserver;

	private final boolean isAsync;

	public WorldTicker(WorldServer worldServer, boolean isAsync) {
		this.worldserver = worldServer;
		this.isAsync = isAsync;
	}

	// This is mostly copied code from world ticking
	@Override
	public void run() {
		// Synchronize for safe teleportation
		synchronized (this.worldserver) {
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

			// Synchronize
			synchronized (WorldTickerManager.LOCK) {
				// this.methodProfiler.b();
				// this.methodProfiler.a("tracker");
				worldserver.timings.tracker.startTiming(); // Spigot
				if (MinecraftServer.getServer().getPlayerList().getPlayerCount() != 0) // Tuinity
				{
					// Tuinity start - controlled flush for entity tracker packets
					List<NetworkManager> disabledFlushes = new java.util.ArrayList<>(
							MinecraftServer.getServer().getPlayerList().getPlayerCount());
					for (EntityPlayer player : MinecraftServer.getServer().getPlayerList().players) {
						PlayerConnection connection = player.playerConnection;
						if (connection != null) {
							connection.networkManager.disableAutomaticFlush();
							disabledFlushes.add(connection.networkManager);
						}
					}
					try {
						worldserver.getTracker().updatePlayers();
					} finally {
						for (NetworkManager networkManager : disabledFlushes) {
							networkManager.enableAutomaticFlush();
						}
					}
					// Tuinity end - controlled flush for entity tracker packets
				}
			}

			worldserver.timings.tracker.stopTiming(); // Spigot
			// this.methodProfiler.b();
			// this.methodProfiler.b();
			worldserver.explosionDensityCache.clear(); // Paper - Optimize explosions
			worldserver.movementCache.clear(); // IonSpigot - Movement Cache

			if (this.isAsync) {
				WorldTickerManager.getInstance().getLatch().decrement();
			}
		}
	}

}

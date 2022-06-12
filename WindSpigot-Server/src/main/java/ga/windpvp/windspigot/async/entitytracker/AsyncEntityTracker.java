package ga.windpvp.windspigot.async.entitytracker;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ga.windpvp.windspigot.async.AsyncUtil;
import ga.windpvp.windspigot.async.ResettableLatch;
import ga.windpvp.windspigot.config.WindSpigotConfig;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.EntityTracker;
import net.minecraft.server.EntityTrackerEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NetworkManager;
import net.minecraft.server.PlayerConnection;
import net.minecraft.server.WorldServer;

public class AsyncEntityTracker extends EntityTracker {
	
	private static final ExecutorService trackingThreadExecutor = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("WindSpigot Entity Tracker Thread").build());
	private static final List<NetworkManager> disabledFlushes = Lists.newArrayList();
	
	private final ResettableLatch latch = new ResettableLatch(6);
	
	public AsyncEntityTracker(WorldServer worldserver) {
		super(worldserver);
	}

	
	@Override
	public void updatePlayers() {	
		int offset = -1;
		
		for (int i = 1; i <= WindSpigotConfig.trackingThreads; i++) {
			final int finalOffset = offset++;
			
			AsyncUtil.run(() -> {
				for (int index = finalOffset; index < c.size(); index += WindSpigotConfig.trackingThreads) {
					c.get(index).update();
				}
				latch.decrement();
			}, trackingThreadExecutor);
			
		}
		try {
			latch.waitTillZero();
			latch.reset();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public ResettableLatch getLatch() {
		return latch;
	}
	
	// Global enabling/disabling of automatic flushing
	
	public static void disableAutomaticFlush() {
		synchronized (disabledFlushes) {
			if (MinecraftServer.getServer().getPlayerList().getPlayerCount() != 0) // Tuinity
			{
				// Tuinity start - controlled flush for entity tracker packets			
				for (EntityPlayer player : MinecraftServer.getServer().getPlayerList().players) {
					PlayerConnection connection = player.playerConnection;
					if (connection != null) {
						connection.networkManager.disableAutomaticFlush();
						disabledFlushes.add(connection.networkManager);
					}
				}
			}
		}
	}
	
	public static void enableAutomaticFlush() {
		synchronized (disabledFlushes) {
			for (NetworkManager networkManager : disabledFlushes) {
				networkManager.enableAutomaticFlush();
			}
			disabledFlushes.clear();
		}
		// Tuinity end - controlled flush for entity tracker packets
	}
	
	public static ExecutorService getExecutor() {
		return trackingThreadExecutor;
	}
}

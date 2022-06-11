package ga.windpvp.windspigot.async.entitytracker;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ga.windpvp.windspigot.async.AsyncUtil;
import ga.windpvp.windspigot.async.ResettableLatch;
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
		for (int offset = 0; offset <= 5; offset++) {
			final int finalOffset = offset;
			
			AsyncUtil.run(() -> {
				for (int index = finalOffset; index <= c.size(); index += 6) {
					EntityTrackerEntry entry = c.get(index);
					entry.update();
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

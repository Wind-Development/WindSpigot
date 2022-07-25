package ga.windpvp.windspigot.async.entitytracker;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ga.windpvp.windspigot.async.AsyncUtil;
import ga.windpvp.windspigot.config.WindSpigotConfig;
import net.minecraft.server.EntityTracker;
import net.minecraft.server.WorldServer;

public class AsyncEntityTracker extends EntityTracker {
	
	private static final ExecutorService trackingThreadExecutor = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("WindSpigot Entity Tracker Thread").build());
	private final WorldServer worldServer;	
	
	public AsyncEntityTracker(WorldServer worldserver) {
		super(worldserver);
		this.worldServer = worldserver;
	}
	
	@Override
	public void updatePlayers() {	
		int offset = 0;
		
		for (int i = 1; i <= WindSpigotConfig.trackingThreads; i++) {
			final int finalOffset = offset++;
			
			AsyncUtil.run(() -> {
				for (int index = finalOffset; index < c.size(); index += WindSpigotConfig.trackingThreads) {
					c.get(index).update();
				}
				worldServer.ticker.getLatch().decrement();

			}, trackingThreadExecutor);
			
		}
	}

	public static ExecutorService getExecutor() {
		return trackingThreadExecutor;
	}
}

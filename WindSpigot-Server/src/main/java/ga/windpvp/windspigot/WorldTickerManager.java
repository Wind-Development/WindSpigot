package ga.windpvp.windspigot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import ga.windpvp.windspigot.async.AsyncUtil;
import ga.windpvp.windspigot.async.world.WorldTicker;
import ga.windpvp.windspigot.config.WindSpigotConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldServer;

public class WorldTickerManager {

	// List of cached world tickers
	private List<WorldTicker> worldTickers = new ArrayList<>();
	
	// Latch to wait for world tick completion
	public static CountDownLatch latch = null;
	
	// Lock for ticking
	public final static Object lock = new Object();
	
	// Caches Runnables for less Object creation
	private void cacheWorlds(boolean isAsync) {
		if (this.worldTickers.size() != MinecraftServer.getServer().worlds.size()) {
			worldTickers.clear();
			for (WorldServer world : MinecraftServer.getServer().worlds) {
				 worldTickers.add(new WorldTicker(world, isAsync));
			}
		}
	}

	// Ticks all worlds
	public void tick() {
		if (!WindSpigotConfig.parallelWorld) {
			
			this.cacheWorlds(false);

			for (WorldTicker ticker : this.worldTickers) {
				ticker.run();
			}
		} else {			
			this.cacheWorlds(true);
			
			latch = new CountDownLatch(worldTickers.size());
			
			for (WorldTicker ticker : this.worldTickers) {
				AsyncUtil.run(ticker);
			}
			
			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
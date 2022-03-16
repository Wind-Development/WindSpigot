package ga.windpvp.windspigot.async.world;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import ga.windpvp.windspigot.async.AsyncUtil;
import ga.windpvp.windspigot.config.WindSpigotConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldServer;

public class WorldTickerManager {

	// List of cached world tickers
	private List<WorldTicker> worldTickers = new ArrayList<>();
	
	// Latch to wait for world tick completion
	public static CountDownLatch latch = null;
	
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
		if (!WindSpigotConfig.parallelWorld || this.worldTickers.size() == 1) {
			
			this.cacheWorlds(false);

			for (WorldTicker ticker : this.worldTickers) {
				ticker.run();
			}
		} else {
			latch = new CountDownLatch(worldTickers.size());
			
			this.cacheWorlds(true);
			
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

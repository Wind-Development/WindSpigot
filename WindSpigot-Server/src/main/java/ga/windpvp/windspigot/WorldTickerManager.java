package ga.windpvp.windspigot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import ga.windpvp.windspigot.async.AsyncUtil;
import ga.windpvp.windspigot.async.ResettableLatch;
import ga.windpvp.windspigot.async.world.WorldTicker;
import ga.windpvp.windspigot.config.WindSpigotConfig;
import javafixes.concurrency.ReusableCountLatch;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldServer;

public class WorldTickerManager {

	// List of cached world tickers
	private List<WorldTicker> worldTickers = new ArrayList<>();

	// Latch to wait for world tick completion
	private final ResettableLatch latch;

	// Lock for ticking
	public final static Object LOCK = new Object();

	// Executor for world ticking
	private final Executor worldTickExecutor = Executors
			.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("WindSpigot Parallel World Thread %d").build());
	
	// Instance
	private static WorldTickerManager worldTickerManagerInstance;
	
	// Initializes the world ticker manager
	public WorldTickerManager() {
		worldTickerManagerInstance = this;
		
		// Initialize the world ticker latch
		if (WindSpigotConfig.parallelWorld) {
			this.latch = new ResettableLatch();
		} else {
			this.latch = null;
		}
	}

	// Caches Runnables for less Object creation
	private void cacheWorlds(boolean isAsync) {
		// Only create new world tickers if needed
		if (this.worldTickers.size() != MinecraftServer.getServer().worlds.size()) {
			worldTickers.clear();
						
			// Create world tickers
			for (WorldServer world : MinecraftServer.getServer().worlds) {
				worldTickers.add(new WorldTicker(world, isAsync));
			}
			
			if (this.latch != null) {
				// Reuse the latch
				this.latch.reset(this.worldTickers.size());
			}
		}
	}

	// Ticks all worlds
	public void tick() {
		if (!WindSpigotConfig.parallelWorld) {

			// Cache world tick runnables if not cached
			this.cacheWorlds(false);

			// Tick each world on one thread
			for (WorldTicker ticker : this.worldTickers) {
				ticker.run();
			}
		} else {
			// Cache world tick runnables if not cached
			this.cacheWorlds(true);

			// Only use multiple threads if there are multiple worlds
			if (this.worldTickers.size() != 1) {
				//latch = new CountDownLatch(worldTickers.size());

				// Tick each world on a reused thread 
				for (WorldTicker ticker : this.worldTickers) {
					AsyncUtil.run(ticker, this.worldTickExecutor);
				}

				try {
					// Wait for worlds to finish ticking then reset latch
					latch.waitTillZero();
					this.latch.reset(this.worldTickers.size());;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				// Tick only world on one thread
				this.worldTickers.get(0).run();
			}
		}
	}

	// Gets the world tick executor
	public Executor getExecutor() {
		return this.worldTickExecutor;
	}
	
	// Gets the count down latch for world ticking
	public ReusableCountLatch getLatch() {
		return this.latch;
	}
	
	// Gets the world ticker manager instance
	public static WorldTickerManager getInstance() {
		return worldTickerManagerInstance;
	}

}

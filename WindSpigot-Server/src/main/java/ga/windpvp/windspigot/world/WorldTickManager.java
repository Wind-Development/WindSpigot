package ga.windpvp.windspigot.world;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import ga.windpvp.windspigot.async.AsyncUtil;
import ga.windpvp.windspigot.async.ResettableLatch;
import ga.windpvp.windspigot.async.entitytracker.ThreadSafeTracker;
import ga.windpvp.windspigot.async.world.AsyncWorldTicker;
import ga.windpvp.windspigot.config.WindSpigotConfig;
import javafixes.concurrency.ReusableCountLatch;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldServer;

public class WorldTickManager {

	// List of cached world tickers
	private final List<WorldTicker> worldTickers = Lists.newCopyOnWriteArrayList();

	// Latch to wait for world tick completion
	private final ResettableLatch latch = new ResettableLatch();

	// Lock for ticking
	public final static Object LOCK = new Object();

	// Executor for world ticking
	private final Executor worldTickExecutor = Executors
			.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("WindSpigot Parallel World Thread %d").build());
	
	// Instance
	private static WorldTickManager worldTickerManagerInstance;
	
	// Cached async entity tracker update runnable
	private final Runnable cachedUpdateTrackerTask = () -> {
		for (WorldTicker ticker : this.worldTickers) {
			ticker.worldserver.getTracker().updatePlayers();
		}
		ThreadSafeTracker.enableAutomaticFlush();
	};
	
	// Initializes the world ticker manager
	public WorldTickManager() {
		worldTickerManagerInstance = this;

	}

	// Caches Runnables for less Object creation
	private void cacheWorlds(boolean isAsync) {
		// Only create new world tickers if needed
		if (this.worldTickers.size() != MinecraftServer.getServer().worlds.size()) {
			worldTickers.clear();
						
			// Create world tickers
			for (WorldServer world : MinecraftServer.getServer().worlds) {
				
				// Decide between creating sync/async world tickers
				if (isAsync) {
					worldTickers.add(new AsyncWorldTicker(world)); 
				} else {
					worldTickers.add(new WorldTicker(world));
				}
				
			}
			if (isAsync) {
				// Reuse the latch
				this.latch.reset(this.worldTickers.size());
			}
		}
	}

	// Ticks all worlds
	public void tick() {
		if (WindSpigotConfig.parallelWorld) {
			tickAsync();
		} else {
			tickSync();
		}
		
		// Handle async entity tracking if needed
		if (!WindSpigotConfig.disableTracking) {
			
			ThreadSafeTracker.disableAutomaticFlush(); // Perform this on the main thread
			AsyncUtil.run(cachedUpdateTrackerTask, ThreadSafeTracker.getExecutor());	
		}
	}
	
	private void tickSync() {
		// Cache world tick runnables if not cached already
		this.cacheWorlds(false); // Cache them as sync world tickers

		// Tick each world on one thread
		for (WorldTicker ticker : this.worldTickers) {
			ticker.run();
		}
	}
	
	private void tickAsync() {
		// Cache world tick runnables if not cached already
		this.cacheWorlds(true); // Cache them as async world tickers

		int asyncTaskCount = worldTickers.size() - 1;
		
		// Tick each world with a reused runnable on its own thread, except the last ticker (that one is run sync)
		for (int index = 0; index < this.worldTickers.size(); index++) { 
			WorldTicker ticker = worldTickers.get(index);
			// Tick all worlds but one on a separate thread
			if (index < asyncTaskCount) {
				AsyncUtil.run(ticker, worldTickExecutor);
			} else {
				// Run the last ticker on the main thread, no need to schedule it async as all
				// other tickers are running already
				ticker.run();
			}
		}

		try {
			// Wait for worlds to finish ticking
			latch.waitTillZero();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			latch.reset(this.worldTickers.size());;
		}
	}

	/**
	 * @return The world tick executor
	 */
	public Executor getExecutor() {
		return this.worldTickExecutor;
	}
	
	/**
	 * @return The count down latch for world ticking
	 */
	public ReusableCountLatch getLatch() {
		return this.latch;
	}
	
	/**
	 * @return The world ticker manager instance
	 */
	public static WorldTickManager getInstance() {
		return worldTickerManagerInstance;
	}

}

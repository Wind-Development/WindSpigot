package ga.windpvp.windspigot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import ga.windpvp.windspigot.async.AsyncUtil;
import ga.windpvp.windspigot.async.world.WorldTicker;
import ga.windpvp.windspigot.config.WindSpigotConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldServer;

public class WorldTickerManager {

	// List of cached world tickers
	private List<WorldTicker> worldTickers = new ArrayList<>();

	// Latch to wait for world tick completion
	public static volatile CountDownLatch latch = null;

	// Lock for ticking
	public final static Object lock = new Object();

	// Executor for world ticking
	private final Executor worldTickExecutor = Executors
			.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("WindSpigot Parallel World Thread").build());

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

			if (this.worldTickers.size() != 1) {
				latch = new CountDownLatch(worldTickers.size());

				for (WorldTicker ticker : this.worldTickers) {
					AsyncUtil.run(ticker, this.worldTickExecutor);
				}

				try {
					latch.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				this.worldTickers.get(0).run();
			}
		}
	}

	public Executor getExecutor() {
		return this.worldTickExecutor;
	}

}

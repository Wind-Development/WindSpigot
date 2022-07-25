package ga.windpvp.windspigot.world;

import java.util.List;
import com.google.common.collect.Lists;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldServer;

public class WorldTickManager {

	// List of cached world tickers
	private final List<WorldTicker> worldTickers = Lists.newArrayList();

	// Instance
	private static WorldTickManager worldTickerManagerInstance;
		
	// Initializes the world ticker manager
	public WorldTickManager() {
		worldTickerManagerInstance = this;

	}

	private void cacheWorlds() {
		// Only create new world tickers if needed
		if (this.worldTickers.size() != MinecraftServer.getServer().worlds.size()) {
			worldTickers.clear();
						
			// Create world tickers
			for (WorldServer world : MinecraftServer.getServer().worlds) {
				WorldTicker ticker = new WorldTicker(world);
				worldTickers.add(ticker);	
				world.ticker = ticker;
			}

		}
	}

	// Ticks all worlds
	public void tick() {
		tickWorlds();
	}
	
	private void tickWorlds() {
		// Cache world tick runnables if not cached already
		this.cacheWorlds();
		
		for (WorldTicker ticker : this.worldTickers) {
			ticker.run();
		}
	}

	/*
	 * The world ticker manager instance
	 */
	public static WorldTickManager getInstance() {
		return worldTickerManagerInstance;
	}
}

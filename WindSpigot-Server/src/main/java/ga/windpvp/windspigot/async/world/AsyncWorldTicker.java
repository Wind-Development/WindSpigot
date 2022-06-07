package ga.windpvp.windspigot.async.world;

import ga.windpvp.windspigot.world.WorldTicker;
import ga.windpvp.windspigot.world.WorldTickManager;
import net.minecraft.server.WorldServer;

// This is just a world ticker, but async
public class AsyncWorldTicker extends WorldTicker {

	public AsyncWorldTicker(WorldServer worldServer) {
		super(worldServer);
	}

	@Override
	public void run() {
		try {
			super.run();
		} finally {
			// Decrement the latch to show that this world is done ticking
			WorldTickManager.getInstance().getLatch().decrement();
		}
	}

}

package ga.windpvp.windspigot.async.entitytracker;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ga.windpvp.windspigot.async.AsyncUtil;
import ga.windpvp.windspigot.config.WindSpigotConfig;
import me.rastrian.dev.utils.IndexedLinkedHashSet;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.EntityTracker;
import net.minecraft.server.EntityTrackerEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldServer;

public class MultithreadedEntityTracker extends EntityTracker {
	
	private static final ExecutorService trackingThreadExecutor = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("WindSpigot Entity Tracker Thread").build());
	private final WorldServer worldServer;	
	
	public MultithreadedEntityTracker(WorldServer worldserver) {
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
                    ((IndexedLinkedHashSet<EntityTrackerEntry>) c).get(index).update();
				}
				worldServer.ticker.getLatch().decrement();

			}, trackingThreadExecutor);
			
		}
		try {
            worldServer.ticker.getLatch().waitTillZero();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	    worldServer.ticker.getLatch().reset();
        for (EntityPlayer player : MinecraftServer.getServer().getPlayerList().players) {
            player.playerConnection.sendQueuedPackets();
        }
	}

	public static ExecutorService getExecutor() {
		return trackingThreadExecutor;
	}
}

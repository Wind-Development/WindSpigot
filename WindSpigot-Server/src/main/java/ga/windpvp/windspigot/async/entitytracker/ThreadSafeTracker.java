package ga.windpvp.windspigot.async.entitytracker;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.minecraft.server.*;

import javax.annotation.concurrent.ThreadSafe;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ga.windpvp.windspigot.async.AsyncUtil.runSynchronized;

/*
 * This is an entity tracker that is thread safe. All public methods accessed by multiple threads 
 * are synchronized if not already synchronized.
 */
@ThreadSafe
public class ThreadSafeTracker extends EntityTracker {
	
	private static final ExecutorService trackingThreadExecutor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("WindSpigot Entity Tracker Thread").build());
	
	private static final List<NetworkManager> disabledFlushes = Lists.newArrayList();
	
	private final ThreadSafeTracker tracker;
	
	public ThreadSafeTracker(WorldServer worldserver) {
		super(worldserver);
		this.tracker = this;
	}
	
	@Override
	public void track(Entity entity) {
		runSynchronized(tracker, () -> super.track(entity));
	}
	
	@Override
	public void addEntity(Entity entity, int i, int j) {
		runSynchronized(tracker, () -> super.addEntity(entity, i, j));
	}
	
	@Override
	public void addEntity(Entity entity, int i, final int j, boolean flag) {
		runSynchronized(tracker, () -> super.addEntity(entity, i, j, flag));
	}
	
	@Override
	public void untrackEntity(Entity entity) {
		runSynchronized(tracker, () -> super.untrackEntity(entity));
	}
	
	@Override
	public void updatePlayers() {
		runSynchronized(tracker, super::updatePlayers);
	}
	
	@Override
	public void a(EntityPlayer entityplayer) {
		runSynchronized(tracker, () -> super.a(entityplayer));
	}
	
	@Override
	public void a(Entity entity, Packet<?> packet) {
		runSynchronized(tracker, () -> super.a(entity, packet));
	}
	
	@Override
	public void sendPacketToEntity(Entity entity, Packet<?> packet) {
		runSynchronized(tracker, () -> super.sendPacketToEntity(entity, packet));
	}
	
	@Override
	public void untrackPlayer(EntityPlayer entityplayer) {
		runSynchronized(tracker, () -> super.untrackPlayer(entityplayer));
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

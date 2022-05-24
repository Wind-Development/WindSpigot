package ga.windpvp.windspigot.async.entitytracker;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.concurrent.ThreadSafe;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.EntityTracker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NetworkManager;
import net.minecraft.server.Packet;
import net.minecraft.server.PlayerConnection;
import net.minecraft.server.WorldServer;

/*
 * This is an entity tracker that is thread safe. All public methods accessed by mutliple threads 
 * are synchronized if not already synchronized.
 */
@ThreadSafe
public class AsyncEntityTracker extends EntityTracker {
	
	// Cache tracking task, we do not need to create a new one each tick
	private final Runnable cachedTrackTask;
	
	private static ExecutorService trackingThreadPool = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("WindSpigot Entity Tracker Thread").build());
	
	private static final List<NetworkManager> disabledFlushes = Lists.newArrayList();
	
	private final AsyncEntityTracker tracker;
	
	public AsyncEntityTracker(WorldServer worldserver) {
		super(worldserver);
		this.tracker = this;
		
		this.cachedTrackTask = () -> {
			synchronized (tracker) {
				synchronized (c) {
					super.updatePlayers();
				}
			}
		};
	}
	
	private boolean synchronize() {
		return !Thread.holdsLock(this);
	}
	
	@Override
	public void track(Entity entity) {
		if (synchronize()) {
			synchronized (this) {
				super.track(entity);
			}
		} else {
			super.track(entity);
		}
	}
	
	@Override
	public void addEntity(Entity entity, int i, int j) {
		if (synchronize()) {
			synchronized (this) {
				super.addEntity(entity, i, j);
			}
		} else {
			super.addEntity(entity, i, j);
		}
	}
	
	@Override
	public void untrackEntity(Entity entity) {
		if (synchronize()) {
			synchronized (this) {
				super.untrackEntity(entity);
			}
		} else {
			super.untrackEntity(entity);
		}
	}
	
	@Override
	public void updatePlayers() {
		trackingThreadPool.submit(cachedTrackTask);
	}
	
	@Override
	public void a(EntityPlayer entityplayer) {
		if (synchronize()) {
			synchronized (this) {
				super.a(entityplayer);
			}
		} else {
			super.a(entityplayer);
		}
	}
	
	@Override
	public void a(Entity entity, Packet<?> packet) {
		if (synchronize()) {
			synchronized (this) {
				super.a(entity, packet);
			}
		} else {
			super.a(entity, packet);
		}
	}
	
	@Override
	public void sendPacketToEntity(Entity entity, Packet<?> packet) {
		if (synchronize()) {
			synchronized (this) {
				super.sendPacketToEntity(entity, packet);
			}
		} else {
			super.sendPacketToEntity(entity, packet);
		}
	}
	
	@Override
	public void untrackPlayer(EntityPlayer entityplayer) {
		if (synchronize()) {
			synchronized (this) {
				super.untrackPlayer(entityplayer);
			}
		} else {
			super.untrackPlayer(entityplayer);
		}
	}
	
	// Global enabling/disabling of automatic flushing
	
	public static void disableAutomaticFlush() {
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
	
	public static void enableAutomaticFlush() {
		for (NetworkManager networkManager : disabledFlushes) {
			networkManager.enableAutomaticFlush();
		}
		disabledFlushes.clear();
		// Tuinity end - controlled flush for entity tracker packets
	}
}

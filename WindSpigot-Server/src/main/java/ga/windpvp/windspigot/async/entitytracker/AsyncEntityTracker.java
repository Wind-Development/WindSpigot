package ga.windpvp.windspigot.async.entitytracker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.concurrent.ThreadSafe;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.EntityTracker;
import net.minecraft.server.Packet;
import net.minecraft.server.WorldServer;

/*
 * This is an entity tracker that performs tracking off the main thread and
 * is thread safe. All public methods are synchronized if not already synchronized.
 */
@ThreadSafe
public class AsyncEntityTracker extends EntityTracker {
	
	// Cache tracking task, we do not need to create a new one each tick
	private final Runnable cachedTrackTask = () -> {
		synchronized (this) { 
			// Updating players is always synchronized, other methods can be called inside
			// updating players code, so they are only synchronized if not already.
			synchronized (c) {
				super.updatePlayers();
				
			}
		}
	};
	private static ExecutorService trackingThreadPool = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("WindSpigot Entity Tracker Thread").build());
	
	public AsyncEntityTracker(WorldServer worldserver) {
		super(worldserver);
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
}

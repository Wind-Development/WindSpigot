package ga.windpvp.windspigot.async.entitytracker.entry;

import java.util.List;

import ga.windpvp.windspigot.async.AsyncUtil;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.EntityTracker;
import net.minecraft.server.EntityTrackerEntry;
import net.minecraft.server.Packet;

/*
 * This is an entry for entity trackers that is thread safe. All public methods accessed by  
 * multiple threads are synchronized if not already synchronized.
 */
public class ThreadSafeTrackerEntry extends EntityTrackerEntry {

	public ThreadSafeTrackerEntry(EntityTracker entityTracker, Entity entity, int b, int c, boolean flag) {
		super(entityTracker, entity, b, c, flag);
	}

	public ThreadSafeTrackerEntry(Entity entity, int b, int c, boolean flag) {
		super(entity, b, c, flag);
	}

	private boolean synchronize() {
		return !Thread.holdsLock(this);
	}

	// These methods return a value, so we manually check for synchronization
	@Override
	public boolean equals(Object object) {
		if (synchronize()) {
			synchronized (this) {
				return super.equals(object);
			}
		} else {
			return super.equals(object);
		}
	}

	@Override
	public int hashCode() {
		if (synchronize()) {
			synchronized (this) {
				return super.hashCode();
			}
		} else {
			return super.hashCode();
		}
	}
	
	@Override
	public boolean c(EntityPlayer entityplayer) {
		if (synchronize()) {
			synchronized (this) {
				return super.c(entityplayer);
			}
		} else {
			return super.c(entityplayer);
		}
	}

	@Override
	public boolean e(EntityPlayer entityplayer) {
		if (synchronize()) {
			synchronized (this) {
				return super.e(entityplayer);
			}
		} else {
			return super.e(entityplayer);
		}
	}

	@Override
	public Packet c() {
		if (synchronize()) {
			synchronized (this) {
				return super.c();
			}
		} else {
			return super.c();
		}
	}

	@Override
	public int getRange() {
		if (synchronize()) {
			synchronized (this) {
				return super.getRange();
			}
		} else {
			return super.getRange();
		}
	}
	
	
	// These methods don't return a value, so we can just use a one-line piece of code to synchronize and run
	@Override
	public void update() {
		AsyncUtil.runSynchronized(this, () -> super.update());
	}

	@Override
	public void processToRemove() {
		AsyncUtil.runSynchronized(this, () -> super.processToRemove());
	}

	@Override
	public void addNearPlayers() {
		AsyncUtil.runSynchronized(this, () -> super.addNearPlayers());
	}

	@Override
	public void track(List<EntityHuman> list) {
		AsyncUtil.runSynchronized(this, () -> super.track(list));
	}

	@Override
	public void broadcast(Packet packet) {
		AsyncUtil.runSynchronized(this, () -> super.broadcast(packet));
	}

	@Override
	public void broadcastIncludingSelf(Packet packet) {
		AsyncUtil.runSynchronized(this, () -> super.broadcastIncludingSelf(packet));
	}

	@Override
	public void a() {
		AsyncUtil.runSynchronized(this, () -> super.a());
	}

	@Override
	public void a(EntityPlayer entityplayer) {
		AsyncUtil.runSynchronized(this, () -> super.a(entityplayer));
	}

	@Override
	public void updatePlayer(EntityPlayer entityplayer) {
		AsyncUtil.runSynchronized(this, () -> super.updatePlayer(entityplayer));
	}

	@Override
	public void clear(EntityPlayer entityplayer) {
		AsyncUtil.runSynchronized(this, () -> super.clear(entityplayer));
	}

}

package ga.windpvp.windspigot.async.entitytracker;

import java.util.List;
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
	public void update() {
		if (synchronize()) {
			synchronized (this) {
				super.update();
			}
		} else {
			super.update();
		}
	}

	@Override
	public void processToRemove() {
		if (synchronize()) {
			synchronized (this) {
				super.processToRemove();
			}
		} else {
			super.processToRemove();
		}
	}

	@Override
	public void addNearPlayers() {
		if (synchronize()) {
			synchronized (this) {
				super.addNearPlayers();
			}
		} else {
			super.addNearPlayers();
		}
	}

	@Override
	public void track(List<EntityHuman> list) {
		if (synchronize()) {
			synchronized (this) {
				super.track(list);
			}
		} else {
			super.track(list);
		}
	}

	@Override
	public void broadcast(Packet packet) {
		if (synchronize()) {
			synchronized (this) {
				super.broadcast(packet);
			}
		} else {
			super.broadcast(packet);
		}
	}

	@Override
	public void broadcastIncludingSelf(Packet packet) {
		if (synchronize()) {
			synchronized (this) {
				super.broadcastIncludingSelf(packet);
			}
		} else {
			super.broadcastIncludingSelf(packet);
		}
	}

	@Override
	public void a() {
		if (synchronize()) {
			synchronized (this) {
				super.a();
			}
		} else {
			super.a();
		}
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
	public void updatePlayer(EntityPlayer entityplayer) {
		if (synchronize()) {
			synchronized (this) {
				super.updatePlayer(entityplayer);
			}
		} else {
			super.updatePlayer(entityplayer);
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

	@Override
	public void clear(EntityPlayer entityplayer) {
		if (synchronize()) {
			synchronized (this) {
				super.clear(entityplayer);
			}
		} else {
			super.clear(entityplayer);
		}
	}

}

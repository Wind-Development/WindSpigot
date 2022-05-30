package ga.windpvp.windspigot.async.entitytracker.entry;

import net.minecraft.server.*;

import javax.annotation.concurrent.ThreadSafe;
import java.util.List;

import static ga.windpvp.windspigot.async.AsyncUtil.runSynchronized;

/*
 * This is an entry for entity trackers that is thread safe. All public methods accessed by  
 * multiple threads are synchronized if not already synchronized.
 */
@ThreadSafe
public class ThreadSafeEntry extends EntityTrackerEntry {

	public ThreadSafeEntry(EntityTracker entityTracker, Entity entity, int b, int c, boolean flag) {
		super(entityTracker, entity, b, c, flag);
	}

	public ThreadSafeEntry(Entity entity, int b, int c, boolean flag) {
		super(entity, b, c, flag);
	}

	@Override
	public boolean equals(Object object) {
		return runSynchronized(this, () -> super.equals(object));
	}

	@Override
	public int hashCode() {
		return runSynchronized(this, super::hashCode);
	}
	
	@Override
	public boolean c(EntityPlayer entityplayer) {
		return runSynchronized(this, () -> super.c(entityplayer));
	}

	@Override
	public boolean e(EntityPlayer entityplayer) {
		return runSynchronized(this, () -> super.e(entityplayer));
	}

	@Override
	public Packet c() {
		return runSynchronized(this, () -> super.c());
	}

	@Override
	public int getRange() {
		return runSynchronized(this, super::getRange);
	}

	@Override
	public void update() {
		runSynchronized(this, super::update);
	}

	@Override
	public void processToRemove() {
		runSynchronized(this, super::processToRemove);
	}

	@Override
	public void addNearPlayers() {
		runSynchronized(this, super::addNearPlayers);
	}

	@Override
	public void track(List<EntityHuman> list) {
		runSynchronized(this, () -> super.track(list));
	}

	@Override
	public void broadcast(Packet packet) {
		runSynchronized(this, () -> super.broadcast(packet));
	}

	@Override
	public void broadcastIncludingSelf(Packet packet) {
		runSynchronized(this, () -> super.broadcastIncludingSelf(packet));
	}

	@Override
	public void a() {
		runSynchronized(this, () -> super.a());
	}

	@Override
	public void a(EntityPlayer entityplayer) {
		runSynchronized(this, () -> super.a(entityplayer));
	}

	@Override
	public void updatePlayer(EntityPlayer entityplayer) {
		runSynchronized(this, () -> super.updatePlayer(entityplayer));
	}

	@Override
	public void clear(EntityPlayer entityplayer) {
		runSynchronized(this, () -> super.clear(entityplayer));
	}

}

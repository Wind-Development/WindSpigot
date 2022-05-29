package ga.windpvp.windspigot.async.entitytracker.entry;

import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

import ga.windpvp.windspigot.async.AsyncUtil;
import me.suicidalkids.ion.visuals.CannonTrackerEntry;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.EntityTracker;

/*
 * This is a fast cannon entity entry for entity trackers that is thread safe. All public methods accessed by  
 * multiple threads are synchronized if not already synchronized.
 */
@ThreadSafe
public class ThreadSafeCannonEntry extends CannonTrackerEntry {

	public ThreadSafeCannonEntry(EntityTracker entityTracker, Entity entity, int i, int j, boolean flag) {
		super(entityTracker, entity, i, j, flag);
	}
	
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
	public void updatePlayer(EntityPlayer entityplayer) {
		AsyncUtil.runSynchronized(this, () -> super.updatePlayer(entityplayer));
	}
}

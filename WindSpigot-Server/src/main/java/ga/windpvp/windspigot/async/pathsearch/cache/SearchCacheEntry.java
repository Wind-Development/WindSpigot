package ga.windpvp.windspigot.async.pathsearch.cache;

import net.minecraft.server.EntityInsentient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PathEntity;

// A cache entry for general targeting to use after an async path search
public class SearchCacheEntry {

	private final EntityInsentient targetingEntity;
	private final PathEntity path;
	private final int tick;

	public SearchCacheEntry(EntityInsentient targetingEntity, PathEntity path) {
		this.targetingEntity = targetingEntity;
		this.path = path;
		this.tick = MinecraftServer.currentTick;
	}

	public EntityInsentient getTargetingEntity() {
		return targetingEntity;
	}
	
	public PathEntity getPath() {
		return path;
	}

	public int getTick() {
		return tick;
	}
	
	public boolean isAccurate() {
		return MinecraftServer.currentTick - tick < 3;
	}

}

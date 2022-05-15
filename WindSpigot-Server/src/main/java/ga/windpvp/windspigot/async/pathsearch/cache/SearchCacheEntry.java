package ga.windpvp.windspigot.async.pathsearch.cache;

import net.minecraft.server.EntityInsentient;
import net.minecraft.server.PathEntity;

// A cache entry for general targeting to use after an async path search
public class SearchCacheEntry {

	private final EntityInsentient targetingEntity;
	private final PathEntity path;

	public SearchCacheEntry(EntityInsentient targetingEntity, PathEntity path) {
		this.targetingEntity = targetingEntity;
		this.path = path;
	}

	public EntityInsentient getTargetingEntity() {
		return targetingEntity;
	}
	
	public PathEntity getPath() {
		return path;
	}

}

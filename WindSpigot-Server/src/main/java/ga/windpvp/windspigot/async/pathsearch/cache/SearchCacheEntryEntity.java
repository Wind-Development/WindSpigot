package ga.windpvp.windspigot.async.pathsearch.cache;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityInsentient;
import net.minecraft.server.PathEntity;

// A cache entry for entity targeting to use after an async path search
public class SearchCacheEntryEntity extends SearchCacheEntry {
	
	private final Entity target;
	
	public SearchCacheEntryEntity(Entity target, EntityInsentient targetingEntity, PathEntity path) {
		super(targetingEntity, path);
		this.target = target;
	}

	public Entity getTarget() {
		return target;
	}
}

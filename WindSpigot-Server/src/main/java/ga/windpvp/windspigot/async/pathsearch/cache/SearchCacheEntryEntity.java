package ga.windpvp.windspigot.async.pathsearch.cache;

import net.minecraft.server.Entity;
import net.minecraft.server.EntityInsentient;
import net.minecraft.server.PathEntity;

// A cache entry for entity targeting to use after an async path search
public class SearchCacheEntryEntity extends SearchCacheEntry {
	
	private final Entity target;
	private final EntityInsentient targetingEntity;
	
	public SearchCacheEntryEntity(Entity target, EntityInsentient targetingEntity, PathEntity path) {
		super(path);
		this.target = target;
		this.targetingEntity = targetingEntity;
	}

	public EntityInsentient getTargetingEntity() {
		return targetingEntity;
	}

	public Entity getTarget() {
		return target;
	}
}

package ga.windpvp.windspigot.async.pathsearch.job;

import java.util.UUID;

import ga.windpvp.windspigot.async.pathsearch.cache.SearchCacheEntry;
import ga.windpvp.windspigot.async.pathsearch.cache.SearchCacheEntryEntity;
import net.minecraft.server.Entity;
import net.minecraft.server.NavigationAbstract;

//This is based on Minetick's async path searching
public class PathSearchJobEntity extends PathSearchJob {

	public Entity target;

	public PathSearchJobEntity(NavigationAbstract navigation, Entity target) {
		super(navigation);
		this.target = target;
	}

	@Override
	public PathSearchJob call() {
		if (!this.issued) {
			this.issued = true;
			this.pathEntity = this.navigation.doPathSearch(this.chunkCache, this.target);
			this.navigation.setSearchResult(this);
			this.cleanup();
		}
		return this;
	}

	@Override
	protected boolean isEntitySearch() {
		return true;
	}

	@Override
	public void cleanup() {
		super.cleanup();
		this.target = null;
	}

	public UUID getCacheEntryKey() {
		return this.target.getUniqueID();
	}

	public SearchCacheEntry getCacheEntryValue() {
		if (this.pathEntity != null) {
			return new SearchCacheEntryEntity(this.navigation.getEntity(), this.target, this.pathEntity);
		}
		return null;
	}
}
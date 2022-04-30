package ga.windpvp.windspigot.async.pathsearch;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import ga.windpvp.windspigot.async.pathsearch.cache.SearchCacheEntry;
import ga.windpvp.windspigot.async.pathsearch.cache.SearchCacheEntryEntity;
import ga.windpvp.windspigot.async.pathsearch.cache.SearchCacheEntryPosition;
import ga.windpvp.windspigot.async.pathsearch.job.PathSearchJob;
import ga.windpvp.windspigot.async.pathsearch.job.PathSearchJobEntity;
import ga.windpvp.windspigot.async.pathsearch.job.PathSearchJobPosition;
import ga.windpvp.windspigot.async.pathsearch.position.PositionPathSearchType;
import ga.windpvp.windspigot.config.WindSpigotConfig;
import net.minecraft.server.BlockPosition;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityInsentient;
import net.minecraft.server.MathHelper;
import net.minecraft.server.Navigation;
import net.minecraft.server.PathEntity;
import net.minecraft.server.World;

// This is based on Minetick's async path searching
public class AsyncNavigation extends Navigation {

	private Map<UUID, SearchCacheEntry> searchCache;
	private Map<PositionPathSearchType, SearchCacheEntryPosition> positionSearchCache;
	private static double minimumDistanceForOffloadingSquared = 0.0D;
	private int cleanUpDelay = 0;
	private final Object jobLock = new Object();
	private PathSearchJob lastQueuedJob;

	public AsyncNavigation(EntityInsentient entityinsentient, World world) {
		super(entityinsentient, world);
		this.searchCache = new HashMap<UUID, SearchCacheEntry>();
		this.positionSearchCache = new HashMap<PositionPathSearchType, SearchCacheEntryPosition>();
	}

	public static void setMinimumDistanceForOffloading(double distance) {
		minimumDistanceForOffloadingSquared = distance * distance;
	}

	private boolean hasAsyncSearchIssued() {
		synchronized (this.jobLock) {
			return this.lastQueuedJob != null;
		}
	}

	private void queueSearch(PathSearchJob job) {
		synchronized (this.jobLock) {
			if (AsyncPathSearchManager.queuePathSearch(job)) {
				this.lastQueuedJob = job;
			}
		}
	}

	private void issueSearch(Entity target) {
		this.queueSearch(new PathSearchJobEntity(this, target));
	}

	private void issueSearch(BlockPosition blockposition, PositionPathSearchType type) {
		this.queueSearch(new PathSearchJobPosition(this, blockposition, type));
	}

	@Override
	public void setSearchResult(PathSearchJobEntity pathSearch) {
		synchronized (this.jobLock) {
			if (this.lastQueuedJob == pathSearch) {
				this.lastQueuedJob = null;
			}
		}
		SearchCacheEntry entry = pathSearch.getCacheEntryValue();
		if (entry != null && entry.didSearchSucceed()) {
			UUID key = pathSearch.getCacheEntryKey();
			synchronized (this.searchCache) {
				this.searchCache.remove(key);
				this.searchCache.put(key, entry);
			}
		}
	}

	@Override
	public void setSearchResult(PathSearchJobPosition pathSearch) {
		synchronized (this.jobLock) {
			if (this.lastQueuedJob == pathSearch) {
				this.lastQueuedJob = null;
			}
		}
		SearchCacheEntryPosition entry = pathSearch.getCacheEntryValue();
		if (entry != null && entry.didSearchSucceed()) {
			synchronized (this.positionSearchCache) {
				PositionPathSearchType key = pathSearch.getCacheEntryKey();
				this.positionSearchCache.remove(key);
				this.positionSearchCache.put(key, entry);
			}
		}
	}

	@Override
	public PathEntity a(Entity entity) {
		if (!this.offloadSearches() || this.b.h(entity) < minimumDistanceForOffloadingSquared) {
			return super.a(entity);
		}
		if (!this.b()) {
			return null;
		}
		SearchCacheEntry entry = null;
		UUID id = entity.getUniqueID();
		synchronized (this.searchCache) {
			if (this.searchCache.containsKey(id)) {
				entry = this.searchCache.get(id);
			}
		}
		PathEntity resultPath = null;
		if (entry != null) {
			resultPath = entry.getAdjustedPathEntity();
			if (!entry.isStillValid()) {
				this.issueSearch(entity);
			}
		}
		if (entry == null && !this.hasAsyncSearchIssued()) {
			resultPath = super.a(entity);
			if (resultPath != null) {
				entry = new SearchCacheEntryEntity(this.b, entity, resultPath);
				synchronized (this.searchCache) {
					SearchCacheEntry oldEntry = this.searchCache.put(id, entry);
					if (oldEntry != null) {
						oldEntry.cleanup();
					}
				}
			}
		}
		return resultPath;
	}

	@Override
	public PathEntity a(BlockPosition blockposition) {
		return this.a(blockposition, PositionPathSearchType.ANYOTHER);
	}

	public PathEntity a(BlockPosition blockposition, PositionPathSearchType type) {
		if (!this.offloadSearches() || this.b.c(blockposition) < minimumDistanceForOffloadingSquared) {
			return super.a(blockposition);
		}
		if (!this.b()) {
			return null;
		}

		SearchCacheEntryPosition entry = null;
		synchronized (this.positionSearchCache) {
			if (this.positionSearchCache.containsKey(type)) {
				entry = this.positionSearchCache.get(type);
			}
		}

		PathEntity resultPath = null;
		if (entry != null) {
			resultPath = entry.getAdjustedPathEntity();
			if (!entry.isStillValid()) {
				this.issueSearch(blockposition, type);
			}
		}
		if (entry == null && !this.hasAsyncSearchIssued()) {
			resultPath = super.a(blockposition);
			if (resultPath != null) {
				entry = new SearchCacheEntryPosition(this.b, blockposition, resultPath);
				synchronized (this.positionSearchCache) {
					SearchCacheEntry oldEntry = this.positionSearchCache.put(type, entry);
					if (oldEntry != null) {
						oldEntry.cleanup();
					}
				}
			}
		}
		return resultPath;
	}

	@Override
	public PathEntity a(double d0, double d1, double d2, PositionPathSearchType type) {
		return this.a(new BlockPosition(MathHelper.floor(d0), (int) d1, MathHelper.floor(d2)), type);
	}

	@Override
	public boolean a(double d0, double d1, double d2, double d3, PositionPathSearchType type) {
		PathEntity pathentity = this.a((double) MathHelper.floor(d0), (double) ((int) d1),
				(double) MathHelper.floor(d2), type);

		return this.a(pathentity, d3);
	}

	public void cleanUpExpiredSearches() {
		this.cleanUpDelay++;
		if (this.cleanUpDelay > 100) {
			this.cleanUpDelay = 0;
			synchronized (this.searchCache) {
				Iterator<Entry<UUID, SearchCacheEntry>> iterator = this.searchCache.entrySet().iterator();
				while (iterator.hasNext()) {
					SearchCacheEntry entry = iterator.next().getValue();
					if (entry.hasExpired()) {
						iterator.remove();
						entry.cleanup();
					} else {
						break;
					}
				}
			}
			synchronized (this.positionSearchCache) {
				Iterator<Entry<PositionPathSearchType, SearchCacheEntryPosition>> iterator = this.positionSearchCache
						.entrySet().iterator();
				while (iterator.hasNext()) {
					SearchCacheEntryPosition entry = iterator.next().getValue();
					if (entry.hasExpired()) {
						iterator.remove();
						entry.cleanup();
					} else {
						break;
					}
				}
			}
		}
	}

	private boolean offloadSearches() {
		return WindSpigotConfig.asyncPathSearches;
	}
}
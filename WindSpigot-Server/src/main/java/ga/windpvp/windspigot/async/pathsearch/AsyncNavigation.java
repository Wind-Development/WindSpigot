package ga.windpvp.windspigot.async.pathsearch;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.bukkit.entity.EntityType;

import com.google.common.collect.Lists;

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
	
	private volatile int cleanUpDelay = 0;
	private PathSearchJob lastQueuedJob;

	private final ReentrantReadWriteLock searchCacheLock;
	private final ReentrantReadWriteLock positionSearchCacheLock;

	private final ReentrantReadWriteLock jobLock;
	
	private static double minimumDistanceForOffloadingSquared = 0.0D;
	private static final List<EntityType> offloadedEntities = Lists.newArrayList();
	
	public AsyncNavigation(EntityInsentient entityinsentient, World world) {
		super(entityinsentient, world);
		this.searchCache = new HashMap<UUID, SearchCacheEntry>();
		this.positionSearchCache = new HashMap<PositionPathSearchType, SearchCacheEntryPosition>();

		if (WindSpigotConfig.asyncPathSearches) {
			searchCacheLock = new ReentrantReadWriteLock();
			positionSearchCacheLock = new ReentrantReadWriteLock();

			jobLock = new ReentrantReadWriteLock();
		} else {
			searchCacheLock = null;
			positionSearchCacheLock = null;

			jobLock = null;
		}
	}
	
	static {
		offloadedEntities.add(EntityType.BAT);
		offloadedEntities.add(EntityType.BLAZE);
		offloadedEntities.add(EntityType.CHICKEN);
		offloadedEntities.add(EntityType.COW);
		offloadedEntities.add(EntityType.CREEPER);
		offloadedEntities.add(EntityType.ENDERMAN);
		offloadedEntities.add(EntityType.HORSE);
		offloadedEntities.add(EntityType.IRON_GOLEM);
		offloadedEntities.add(EntityType.MAGMA_CUBE);
		offloadedEntities.add(EntityType.MUSHROOM_COW);
		offloadedEntities.add(EntityType.PIG);
		offloadedEntities.add(EntityType.PIG_ZOMBIE);
		offloadedEntities.add(EntityType.RABBIT);
		offloadedEntities.add(EntityType.SHEEP);
		offloadedEntities.add(EntityType.SKELETON);
		offloadedEntities.add(EntityType.SILVERFISH);
		offloadedEntities.add(EntityType.SLIME);
		offloadedEntities.add(EntityType.SNOWMAN);
		offloadedEntities.add(EntityType.SQUID);
		offloadedEntities.add(EntityType.WITCH);
		offloadedEntities.add(EntityType.ZOMBIE);
	}

	public static void setMinimumDistanceForOffloading(double distance) {
		minimumDistanceForOffloadingSquared = distance * distance;
	}

	private boolean hasAsyncSearchIssued() {
		jobLock.readLock().lock();
		try {
			return this.lastQueuedJob != null;
		} finally {
			jobLock.readLock().unlock();
		}
	}

	private void queueSearch(PathSearchJob job) {
		if (AsyncPathSearchManager.queuePathSearch(job)) {
			jobLock.writeLock().lock();
			try {
			this.lastQueuedJob = job;
			} finally {
				jobLock.writeLock().unlock();
			}
		}
	}

	private void issueSearch(Entity target) {
		this.queueSearch(new PathSearchJobEntity(this, target));
	}

	/*
	private void issueSearch(BlockPosition blockposition, PositionPathSearchType type) {
		//this.queueSearch(new PathSearchJobPosition(this, blockposition, type));
		this.issueSearch(blockposition.getX(), blockposition.getY(), blockposition.getZ(), type);
	}*/
	
	private void issueSearch(double x, double y, double z, PositionPathSearchType type) {
		this.queueSearch(new PathSearchJobPosition(this, MathHelper.floor(x), (int) y, MathHelper.floor(z), type));
	}

	@Override
	public void setSearchResult(PathSearchJobEntity pathSearch) {
		jobLock.writeLock().lock();
		try {
			if (this.lastQueuedJob == pathSearch) {
				this.lastQueuedJob = null;
			}
		} finally {
			jobLock.writeLock().unlock();
		}

		SearchCacheEntry entry = pathSearch.getCacheEntryValue();
		if (entry != null && entry.didSearchSucceed()) {

			UUID key = pathSearch.getCacheEntryKey();

			searchCacheLock.writeLock().lock();
			try {
				this.searchCache.remove(key);
				this.searchCache.put(key, entry);
			} finally {
				searchCacheLock.writeLock().unlock();
			}
		}
	}

	@Override
	public void setSearchResult(PathSearchJobPosition pathSearch) {
		jobLock.writeLock().lock();
		try {
			if (this.lastQueuedJob == pathSearch) {
				this.lastQueuedJob = null;
			}
		} finally {
			jobLock.writeLock().unlock();
		}

		SearchCacheEntryPosition entry = pathSearch.getCacheEntryValue();

		if (entry != null && entry.didSearchSucceed()) {

			positionSearchCacheLock.writeLock().lock();
			try {
				PositionPathSearchType key = pathSearch.getCacheEntryKey();
				this.positionSearchCache.remove(key);
				this.positionSearchCache.put(key, entry);
			} finally {
				positionSearchCacheLock.writeLock().unlock();
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

		// We use a read lock so multiple threads can read without blocking
		searchCacheLock.readLock().lock();
		try {
			if (this.searchCache.containsKey(id)) {
				entry = this.searchCache.get(id);
			}
		} finally {
			searchCacheLock.readLock().unlock();
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

				SearchCacheEntry oldEntry = null;

				searchCacheLock.writeLock().lock();
				try {
					oldEntry = this.searchCache.put(id, entry);
				} finally {
					searchCacheLock.writeLock().unlock();
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
		return this.a(blockposition.getX(), blockposition.getY(), blockposition.getZ(), PositionPathSearchType.ANYOTHER);
	}
	
	@Override
	public PathEntity a(int x, int y, int z) {
		return this.a(x, y, z, PositionPathSearchType.ANYOTHER);
	}

	public PathEntity a(int x, int y, int z, PositionPathSearchType type) {
		if (!this.offloadSearches() || this.b.distanceSquared(x, y, z) < minimumDistanceForOffloadingSquared) {
			return super.a(x, y, z);
		}
		if (!this.b()) {
			return null;
		}

		SearchCacheEntryPosition entry = null;

		positionSearchCacheLock.readLock().lock();
		try {
			if (this.positionSearchCache.containsKey(type)) {
				entry = this.positionSearchCache.get(type);
			}
		} finally {
			positionSearchCacheLock.readLock().unlock();
		}

		PathEntity resultPath = null;
		if (entry != null) {
			resultPath = entry.getAdjustedPathEntity();
			if (!entry.isStillValid()) {
				this.issueSearch(x, y, z, type);
			}
		}
		if (entry == null && !this.hasAsyncSearchIssued()) {
			resultPath = super.a(x, y, z);
			if (resultPath != null) {
				entry = new SearchCacheEntryPosition(this.b, x, y, z, resultPath);

				SearchCacheEntry oldEntry = null;

				positionSearchCacheLock.writeLock().lock();
				try {
					oldEntry = this.positionSearchCache.put(type, entry);
				} finally {
					positionSearchCacheLock.writeLock().unlock();
					if (oldEntry != null) {
						oldEntry.cleanup();
					}
				}
			}
		}
		return resultPath;
	}

	/*
	@Override
	public PathEntity a(double d0, double d1, double d2, PositionPathSearchType type) {
		return this.a(new BlockPosition(MathHelper.floor(d0), (int) d1, MathHelper.floor(d2)), type);
	}
	*/

	@Override
	public boolean a(double d0, double d1, double d2, double d3, PositionPathSearchType type) {
		PathEntity pathentity = this.a((double) MathHelper.floor(d0), (double) ((int) d1),
				(double) MathHelper.floor(d2), type);

		return this.a(pathentity, d3);
	}

	public void cleanUpExpiredSearches() {
		this.cleanUpDelay = this.cleanUpDelay + 1;
		if (this.cleanUpDelay > 125) { // Clear cache every 125 ticks
			this.cleanUpDelay = 0;

			searchCacheLock.writeLock().lock();
			try {
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
			} finally {
				searchCacheLock.writeLock().unlock();
			}

			positionSearchCacheLock.writeLock().lock();
			try {
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
			} finally {
				positionSearchCacheLock.writeLock().unlock();
			}
		}
	}

	private boolean offloadSearches() {
		if (WindSpigotConfig.asyncPathSearches) {
			return offloadedEntities.contains(b.getBukkitEntity().getType());
		} 
		return false;
	}
}
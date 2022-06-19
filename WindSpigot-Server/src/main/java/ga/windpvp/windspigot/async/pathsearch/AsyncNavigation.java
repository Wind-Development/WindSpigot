package ga.windpvp.windspigot.async.pathsearch;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.entity.EntityType;

import com.google.common.collect.Lists;

import ga.windpvp.windspigot.async.pathsearch.cache.SearchCacheEntry;
import ga.windpvp.windspigot.async.pathsearch.cache.SearchCacheEntryEntity;
import ga.windpvp.windspigot.async.pathsearch.cache.SearchCacheEntryPosition;
import ga.windpvp.windspigot.config.WindSpigotConfig;
import net.minecraft.server.BlockPosition;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityInsentient;
import net.minecraft.server.Navigation;
import net.minecraft.server.PathEntity;
import net.minecraft.server.World;

/*
 *  A replacement for normal entity navigation that performs path searching async
 *  
 *  This is way faster than sync navigation (can handle thousands of entities with full AI), but has a few disadvantages.
 *  Entities' AI can be delayed for up to 2 ticks (very unlikely, but possible), so the cached target path might become inaccurate 
 *  after this time period, but most servers do not need to have perfectly accurate entity navigation. I believe the enhanced
 *  performance is worth it.
 *  
 *  This system performs an async calculation without using it when a entity should perform and use a sync calculation. We start an async
 *  calculation task when a path search is requested, then we return the result of the earlier calculation when a path search is requested again.
 *  This means that the entity does not do anything in terms of targeting for the first tick. If the calculations were not completed within
 *  2 ticks, the server will perform the path search on the main thread.
 *  
 */
public class AsyncNavigation extends Navigation {

	private final List<SearchCacheEntryEntity> searchCache = Lists.newCopyOnWriteArrayList();
	private final List<SearchCacheEntryPosition> positionSearchCache = Lists.newCopyOnWriteArrayList();
	
	public final AtomicBoolean isSearching = new AtomicBoolean(false);
	
	private int ticksSinceCleanup = 0;
	
	private static final List<EntityType> offloadedEntities = Lists.newArrayList();
	private static int minimumDistanceForOffloadingSquared = 0;

	public AsyncNavigation(EntityInsentient var1, World var2) {
		super(var1, var2);
	}
	
	public static void addOffloadedEntities(List<EntityType> entities) {
		offloadedEntities.addAll(entities);
	}
	
	private void issueSearch(Entity targetEntity) {
		SearchHandler.getInstance().issueSearch(targetEntity, this);
	}
	
	private void issueSearch(int x, int y, int z) {
		SearchHandler.getInstance().issueSearch(x, y, z, this);
	}
	
	@Override
	public PathEntity a(Entity targetEntity) {
		
		boolean isTooClose = this.b.h(targetEntity) < minimumDistanceForOffloadingSquared;
		boolean alreadySearching = this.isSearching.get();
		
		if ((!offLoadedSearches(this.getEntity().getBukkitEntity().getType()) || isTooClose) && !alreadySearching) {
			return super.a(targetEntity);
		}
				
		PathEntity finalPath = null;
		
		for (SearchCacheEntryEntity cacheEntry : this.searchCache) {
			if (cacheEntry.getTargetingEntity() == this.getEntity()) {
				finalPath = cacheEntry.getPath();
				
				if (WindSpigotConfig.ensurePathSearchAccuracy) {
					
					// Perform sync if server cannot process an accurate async pathfind in time
					if (!cacheEntry.isAccurate()) {
						return super.a(targetEntity);
					}
				}
				
				break;
			}
		}
		
		if (finalPath == null && !this.isSearching.get()) {
			this.issueSearch(targetEntity);
		}
		
		return finalPath;
	}
	
	@Override
	public PathEntity a(int x, int y, int z) {
		
		boolean isTooClose = this.b.distanceSquared(x, y, z) < minimumDistanceForOffloadingSquared;
		boolean alreadySearching = this.isSearching.get();
		
		if ((!offLoadedSearches(this.getEntity().getBukkitEntity().getType()) || isTooClose) && !alreadySearching) {
			return super.a(new BlockPosition(x, y, z));
		}
				
		PathEntity finalPath = null;
		
		for (SearchCacheEntryPosition cacheEntry : this.positionSearchCache) {
			if (cacheEntry.getTargetingEntity() == this.getEntity()) {
				finalPath = cacheEntry.getPath();
				
				if (WindSpigotConfig.ensurePathSearchAccuracy) {
					
					// Perform sync if server cannot process an accurate async pathfind in time
					if (!cacheEntry.isAccurate()) {
						return super.a(new BlockPosition(x, y, z));
					}
				}
				
				break;
			}
		}
		
		if (finalPath == null && !this.isSearching.get()) {
			this.issueSearch(x, y, z);
		}
		
		return finalPath;
	}
	
	@Override
	public PathEntity a(BlockPosition blockposition) {
		return a(blockposition.getX(), blockposition.getY(), blockposition.getZ());
	}
	
	public void addEntry(SearchCacheEntry cacheEntry) {
		if (cacheEntry instanceof SearchCacheEntryEntity) {
			this.searchCache.add((SearchCacheEntryEntity) cacheEntry);
		} else {
			this.positionSearchCache.add((SearchCacheEntryPosition) cacheEntry);
		}
	}
	
	@Override
	public void cleanUpExpiredSearches() {
		this.ticksSinceCleanup++;
		if (this.ticksSinceCleanup == 150) {
			this.ticksSinceCleanup = 0;
			
			this.searchCache.clear();
			this.positionSearchCache.clear();
		}
	}

	private static boolean offLoadedSearches(EntityType type) {
		if (WindSpigotConfig.asyncPathSearches) {
			return offloadedEntities.contains(type);
		} else {
			return false;
		}
	}

	public static void setMinimumDistanceForOffloading(int distanceToAsync) {
		minimumDistanceForOffloadingSquared = distanceToAsync;
	}	
}

package ga.windpvp.windspigot.async.pathsearch;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.entity.EntityType;

import com.google.common.collect.Lists;

import ga.windpvp.windspigot.async.pathsearch.cache.SearchCacheEntry;
import ga.windpvp.windspigot.async.pathsearch.cache.SearchCacheEntryEntity;
import ga.windpvp.windspigot.async.pathsearch.cache.SearchCacheEntryPosition;
import ga.windpvp.windspigot.config.WindSpigotConfig;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityInsentient;
import net.minecraft.server.Navigation;
import net.minecraft.server.PathEntity;
import net.minecraft.server.World;

// WIP: a replacement for normal entity navigation that is async
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
	
	private void issueSearch(Entity targetEntity) {
		SearchHandler.getInstance().issueSearch(targetEntity, this);
	}
	
	private void issueSearch(int x, int y, int z) {
		SearchHandler.getInstance().issueSearch(x, y, z, this);
	}
	
	@Override
	public PathEntity a(Entity targetEntity) {
		if (!offLoadedSearches(this.getEntity().getBukkitEntity().getType()) || this.b.h(targetEntity) < minimumDistanceForOffloadingSquared && !this.isSearching.get()) {
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
		if (!offLoadedSearches(this.getEntity().getBukkitEntity().getType()) || this.b.distanceSquared(x, y, z) < minimumDistanceForOffloadingSquared && !this.isSearching.get()) {
			return super.a(x, y, z);
		}
				
		PathEntity finalPath = null;
		
		for (SearchCacheEntryPosition cacheEntry : this.positionSearchCache) {
			if (cacheEntry.getTargetingEntity() == this.getEntity()) {
				finalPath = cacheEntry.getPath();
				
				if (WindSpigotConfig.ensurePathSearchAccuracy) {
					
					// Perform sync if server cannot process an accurate async pathfind in time
					if (!cacheEntry.isAccurate()) {
						return super.a(x, y, z);
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

	// TODO: add configuration for this
	private static boolean offLoadedSearches(EntityType type) {
		return offloadedEntities.contains(type);
	}

	public static void setMinimumDistanceForOffloading(int distanceToAsync) {
		minimumDistanceForOffloadingSquared = distanceToAsync;
	}	
}

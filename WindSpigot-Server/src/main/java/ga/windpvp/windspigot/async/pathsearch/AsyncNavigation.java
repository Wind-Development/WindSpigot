package ga.windpvp.windspigot.async.pathsearch;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.entity.EntityType;

import com.google.common.collect.Lists;

import ga.windpvp.windspigot.async.pathsearch.cache.SearchCacheEntryEntity;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityInsentient;
import net.minecraft.server.Navigation;
import net.minecraft.server.PathEntity;
import net.minecraft.server.World;

// WIP: a replacement for normal entity navigation that is async
public class AsyncNavigation extends Navigation {

	private List<SearchCacheEntryEntity> searchCache = Lists.newCopyOnWriteArrayList();
	public AtomicBoolean isSearching = new AtomicBoolean(false);
	
	private int ticksSinceCleanup = 0;
	
	private static List<EntityType> offloadedEntities = Lists.newArrayList();
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
	
	@Override
	public PathEntity a(Entity targetEntity) {
		if (!offLoadedSearches(this.getEntity().getBukkitEntity().getType()) || this.b.h(targetEntity) < minimumDistanceForOffloadingSquared) {
			return super.a(targetEntity);
		}
		
		boolean wasFound = false;
		
		PathEntity finalPath = null;
		
		for (SearchCacheEntryEntity cacheEntry : this.searchCache) {
			if (cacheEntry.getTargetingEntity() == this.getEntity()) {
				wasFound = true;
				finalPath = cacheEntry.getPath();
				break;
			}
		}
		
		if (!wasFound && !this.isSearching.get()) {
			this.issueSearch(targetEntity);
		}
		
		return finalPath;
	}
	
	public void addEntry(SearchCacheEntryEntity cacheEntry) {
		this.searchCache.add(cacheEntry);
	}
	
	@Override
	public void cleanUpExpiredSearches() {
		this.ticksSinceCleanup++;
		if (this.ticksSinceCleanup >= 150) {
			this.ticksSinceCleanup = 0;
			this.searchCache.clear();
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

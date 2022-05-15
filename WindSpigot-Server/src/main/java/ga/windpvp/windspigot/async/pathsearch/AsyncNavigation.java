package ga.windpvp.windspigot.async.pathsearch;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.entity.EntityType;

import com.google.common.collect.Lists;

import ga.windpvp.windspigot.WindSpigot;
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
	
	public AsyncNavigation(EntityInsentient var1, World var2) {
		super(var1, var2);
	}
	
	private void issueSearch(Entity targetEntity) {
		WindSpigot.getInstance().getSearchHandler().issueSearch(targetEntity, this);
	}
	
	@Override
	public PathEntity a(Entity targetEntity) {
		if (!offLoadedSearches(this.getEntity().getBukkitEntity().getType())) {
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

	// TODO: implement this
	private static boolean offLoadedSearches(EntityType type) {
		return true;
	}

	// TODO: implement this
	public static void setMinimumDistanceForOffloading(int distanceToAsync) {
		
	}	
	
}

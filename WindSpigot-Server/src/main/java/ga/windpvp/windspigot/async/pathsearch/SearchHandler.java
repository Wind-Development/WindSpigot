package ga.windpvp.windspigot.async.pathsearch;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ga.windpvp.windspigot.async.AsyncUtil;
import ga.windpvp.windspigot.async.pathsearch.cache.SearchCacheEntryEntity;
import ga.windpvp.windspigot.config.WindSpigotConfig;
import net.minecraft.server.Entity;
import net.minecraft.server.MathHelper;
import net.minecraft.server.PathEntity;

// WIP: async entity path searches
public class SearchHandler {

	private static SearchHandler INSTANCE;
	private final ExecutorService executor = Executors.newFixedThreadPool(WindSpigotConfig.pathSearchThreads);

	public SearchHandler() {
		INSTANCE = this;
	}

	public void issueSearch(Entity targetEntity, AsyncNavigation navigation) {
		navigation.isSearching.set(true);

		AsyncUtil.run(() -> {
			
			PathEntity path = navigation.doPathSearch(navigation.createChunkCache(true),
					MathHelper.floor(targetEntity.locX), MathHelper.floor(targetEntity.locY) + 1,
					MathHelper.floor(targetEntity.locZ));
			SearchCacheEntryEntity cache = new SearchCacheEntryEntity(targetEntity, navigation.getEntity(), path);

			navigation.addEntry(cache);
			
			navigation.isSearching.set(false);

		}, executor);
	}

	public static SearchHandler getInstance() {
		return INSTANCE;
	}

}

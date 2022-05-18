package ga.windpvp.windspigot.async.pathsearch;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import ga.windpvp.windspigot.async.AsyncUtil;
import ga.windpvp.windspigot.async.pathsearch.cache.SearchCacheEntryEntity;
import ga.windpvp.windspigot.async.pathsearch.cache.SearchCacheEntryPosition;
import ga.windpvp.windspigot.config.WindSpigotConfig;
import net.minecraft.server.ChunkCache;
import net.minecraft.server.Entity;
import net.minecraft.server.MathHelper;
import net.minecraft.server.PathEntity;

public class SearchHandler {

	private static SearchHandler INSTANCE;
	private final ExecutorService executor = Executors.newFixedThreadPool(WindSpigotConfig.pathSearchThreads,
			new ThreadFactoryBuilder().setNameFormat("WindSpigot Entity Path Search Thread %d").build());

	public SearchHandler() {
		INSTANCE = this;
	}

	public void issueSearch(Entity targetEntity, AsyncNavigation navigation) {
		
		final ChunkCache chunkCache = navigation.createChunkCache(true);
		
		if (chunkCache == null) {
			return;
		}
		
		navigation.isSearching.set(true);

		AsyncUtil.run(() -> {
			
			PathEntity path = navigation.doPathSearch(chunkCache, MathHelper.floor(targetEntity.locX),
					MathHelper.floor(targetEntity.locY) + 1, MathHelper.floor(targetEntity.locZ));
			SearchCacheEntryEntity cache = new SearchCacheEntryEntity(targetEntity, navigation.getEntity(), path);

			navigation.addEntry(cache);
			
			navigation.isSearching.set(false);

		}, executor);
	}

	public static SearchHandler getInstance() {
		return INSTANCE;
	}

	public void issueSearch(int x, int y, int z, AsyncNavigation navigation) {

		final ChunkCache chunkCache = navigation.createChunkCache(false);
		
		if (chunkCache == null) {
			return;
		}
		
		navigation.isSearching.set(true);
		
		AsyncUtil.run(() -> {
			
			PathEntity path = navigation.doPathSearch(chunkCache, x, y, z);
			SearchCacheEntryPosition cache = new SearchCacheEntryPosition(x, y, z, navigation.getEntity(), path);

			navigation.addEntry(cache);
			
			navigation.isSearching.set(false);

		}, executor);
	}

}

package com.windpvp.windspigot.async.pathsearch;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.windpvp.windspigot.async.AsyncUtil;
import com.windpvp.windspigot.async.pathsearch.cache.SearchCacheEntryEntity;
import com.windpvp.windspigot.async.pathsearch.cache.SearchCacheEntryPosition;
import com.windpvp.windspigot.config.WindSpigotConfig;

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
		
		final int finalX = MathHelper.floor(targetEntity.locX);
		final int finalY = MathHelper.floor(targetEntity.locY) + 1;
		final int finalZ = MathHelper.floor(targetEntity.locZ);
		
		// WindSpigot start - GamingOP69 - ensure isSearching resets in finally block
		// Also guard against RejectedExecutionException: if the executor rejects the
		// task (e.g. during server shutdown), the lambda finally block never runs and
		// isSearching would be permanently stuck at true, freezing the mob's AI.
		try {
			AsyncUtil.run(() -> {
				try {
					PathEntity path = navigation.doPathSearch(chunkCache, finalX, finalY, finalZ);
					SearchCacheEntryEntity cache = new SearchCacheEntryEntity(targetEntity, navigation.getEntity(), path);
					navigation.addEntry(cache);
				} catch (Throwable t) {
					t.printStackTrace();
				} finally {
					navigation.isSearching.set(false);
				}
			}, executor);
		} catch (java.util.concurrent.RejectedExecutionException e) {
			// Executor shut down — reset flag immediately so mob AI is not frozen
			navigation.isSearching.set(false);
		}
		// WindSpigot end - GamingOP69
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
		
		// WindSpigot start - GamingOP69 - ensure isSearching resets in finally block
		try {
			AsyncUtil.run(() -> {
				try {
					PathEntity path = navigation.doPathSearch(chunkCache, x, y, z);
					SearchCacheEntryPosition cache = new SearchCacheEntryPosition(x, y, z, navigation.getEntity(), path);
					navigation.addEntry(cache);
				} catch (Throwable t) {
					t.printStackTrace();
				} finally {
					navigation.isSearching.set(false);
				}
			}, executor);
		} catch (java.util.concurrent.RejectedExecutionException e) {
			// Executor shut down — reset flag immediately so mob AI is not frozen
			navigation.isSearching.set(false);
		}
		// WindSpigot end - GamingOP69
	}

}

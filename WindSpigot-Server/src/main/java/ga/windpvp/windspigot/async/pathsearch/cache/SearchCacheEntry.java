package ga.windpvp.windspigot.async.pathsearch.cache;

import net.minecraft.server.PathEntity;

// A cache entry for entity targeting to use after an async path search
public class SearchCacheEntry {

	private final PathEntity path;
	
	public SearchCacheEntry(PathEntity path) {
		this.path = path;
	}

	public PathEntity getPath() {
		return path;
	}

}

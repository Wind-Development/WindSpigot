package ga.windpvp.windspigot.async.pathsearch.job;

import java.util.concurrent.Callable;

import net.minecraft.server.ChunkCache;
import net.minecraft.server.NavigationAbstract;
import net.minecraft.server.PathEntity;

//This is based on Minetick's async path searching
public abstract class PathSearchJob implements Callable<PathSearchJob> {

	public NavigationAbstract navigation;
	protected ChunkCache chunkCache;
	protected boolean issued;
	private int hashCode;
	protected PathEntity pathEntity;

	public PathSearchJob(NavigationAbstract navigation) {
		this.navigation = navigation;
		this.hashCode = this.navigation.hashCode();
		this.issued = false;
		this.chunkCache = this.navigation.createChunkCache(this.isEntitySearch());
	}

	protected boolean isEntitySearch() {
		return false;
	}

	public void cleanup() {
		this.navigation = null;
		this.chunkCache = null;
		this.pathEntity = null;
	}

	@Override
	public int hashCode() {
		return this.hashCode;
	}

	public final int getSearchHash() {
		return this.hashCode;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof PathSearchJob)) {
			return false;
		}
		return this.hashCode() == ((PathSearchJob) o).hashCode();
	}
}
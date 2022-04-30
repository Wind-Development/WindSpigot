package ga.windpvp.windspigot.async.pathsearch.job;

import ga.windpvp.windspigot.async.pathsearch.cache.SearchCacheEntryPosition;
import ga.windpvp.windspigot.async.pathsearch.position.PositionPathSearchType;
import net.minecraft.server.BlockPosition;
import net.minecraft.server.NavigationAbstract;

//This is based on Minetick's async path searching
public class PathSearchJobPosition extends PathSearchJob {

	private PositionPathSearchType type;
	private BlockPosition blockposition;

	public PathSearchJobPosition(NavigationAbstract navigation, BlockPosition blockposition,
			PositionPathSearchType type) {
		super(navigation);
		this.blockposition = blockposition;
		this.type = type;
	}

	@Override
	public void run() {
		if (!this.issued) {
			this.issued = true;
			this.pathEntity = this.navigation.doPathSearch(this.chunkCache, this.blockposition);
			this.navigation.setSearchResult(this);
			this.cleanup();
		}
	}

	public PositionPathSearchType getCacheEntryKey() {
		return this.type;
	}

	public SearchCacheEntryPosition getCacheEntryValue() {
		if (this.pathEntity != null) {
			return new SearchCacheEntryPosition(this.navigation.getEntity(), this.blockposition, this.pathEntity);
		}
		return null;
	}

	@Override
	public int hashCode() {
		return this.type.hashCode() ^ (super.hashCode() << 4);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof PathSearchJobPosition)) {
			return false;
		}
		PathSearchJobPosition other = (PathSearchJobPosition) o;
		return this.type.equals(other.type) && this.hashCode() == other.hashCode();
	}

	@Override
	public void cleanup() {
		super.cleanup();
		this.blockposition = null;
	}
}
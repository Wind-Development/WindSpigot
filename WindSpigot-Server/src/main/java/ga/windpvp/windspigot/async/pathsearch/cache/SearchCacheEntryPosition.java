package ga.windpvp.windspigot.async.pathsearch.cache;

import net.minecraft.server.BlockPosition;
import net.minecraft.server.EntityInsentient;
import net.minecraft.server.PathEntity;

//This is based on Minetick's async path searching
public class SearchCacheEntryPosition extends SearchCacheEntry {
    
    public SearchCacheEntryPosition(EntityInsentient entity, BlockPosition blockposition, PathEntity path) {
        super(entity, path);
        this.positionTarget = blockposition;
    }

    @Override
    public boolean isStillValid() {
        if(super.isStillValid()) {
            return this.getBlockPosition(this.entity).equals(this.positionStart);
        }
        return false;
    }
}
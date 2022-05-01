package ga.windpvp.windspigot.async.pathsearch.cache;

import net.minecraft.server.BlockPosition;
import net.minecraft.server.EntityInsentient;
import net.minecraft.server.PathEntity;

//This is based on Minetick's async path searching
public class SearchCacheEntryPosition extends SearchCacheEntry {
    
    public SearchCacheEntryPosition(EntityInsentient entity, BlockPosition blockposition, PathEntity path) {
        super(entity, path);
		this.targetX = blockposition.getX();
		this.targetY = blockposition.getY();
		this.targetZ = blockposition.getZ();
    }
    
    public SearchCacheEntryPosition(EntityInsentient entity, int x, int y, int z, PathEntity path) {
		super(entity, path);
		this.targetX = x;
		this.targetY = y;
		this.targetZ = z;
	}

    @Override
    public boolean isStillValid() {
        if (super.isStillValid()) {
            //return this.getBlockPosition(this.entity).equals(this.positionStart);
        	return (entity.locX == startX && entity.locY == startY + 1 && entity.locZ == startZ);
        }
        return false;
    }
}
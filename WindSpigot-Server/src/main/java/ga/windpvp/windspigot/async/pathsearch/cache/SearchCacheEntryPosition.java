package ga.windpvp.windspigot.async.pathsearch.cache;

import ga.windpvp.windspigot.config.WindSpigotConfig;
import net.minecraft.server.EntityInsentient;
import net.minecraft.server.MathHelper;
import net.minecraft.server.PathEntity;

//This is based on Minetick's async path searching
public class SearchCacheEntryPosition extends SearchCacheEntry {
    
    public SearchCacheEntryPosition(EntityInsentient entity, int x, int y, int z, PathEntity path) {
		super(entity, path);
		this.targetX = x;
		this.targetY = y;
		this.targetZ = z;
	}

    @Override
    public boolean isStillValid() {
        if (super.isStillValid()) {
        	if (!WindSpigotConfig.ensurePathSearchAccuracy) {
        		return true;
        	}
        	return (MathHelper.floor(entity.locX) == startX && MathHelper.floor(entity.locY) == startY + 1 && MathHelper.floor(entity.locZ) == startZ);
        }
        return false;
    }
}
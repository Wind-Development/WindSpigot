package ga.windpvp.windspigot.async.pathsearch.cache;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityInsentient;
import net.minecraft.server.MathHelper;
import net.minecraft.server.PathEntity;

//This is based on Minetick's async path searching
public class SearchCacheEntryEntity extends SearchCacheEntry {

    private Entity target;

    public SearchCacheEntryEntity(EntityInsentient entity, Entity target, PathEntity path) {
        super(entity, path);
        this.target = target;
        //this.positionTarget = this.getBlockPosition(this.target);
        this.targetX = MathHelper.floor(target.locX);
        this.targetY = MathHelper.floor(target.locY) + 1;
        this.targetZ = MathHelper.floor(target.locZ);
    }

    @Override
    public boolean isStillValid() {
        if(super.isStillValid()) {
        	return (MathHelper.floor(target.locX) == targetX && MathHelper.floor(target.locY) == targetY + 1 && MathHelper.floor(target.locZ) == targetZ);
        }
        return false;
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        this.target = null;
    }
}
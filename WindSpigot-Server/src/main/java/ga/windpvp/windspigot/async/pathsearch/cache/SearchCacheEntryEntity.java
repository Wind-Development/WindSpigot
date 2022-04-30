package ga.windpvp.windspigot.async.pathsearch.cache;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityInsentient;
import net.minecraft.server.PathEntity;

//This is based on Minetick's async path searching
public class SearchCacheEntryEntity extends SearchCacheEntry {

    private Entity target;

    public SearchCacheEntryEntity(EntityInsentient entity, Entity target, PathEntity path) {
        super(entity, path);
        this.target = target;
        this.positionTarget = this.getBlockPosition(this.target);
    }

    @Override
    public boolean isStillValid() {
        if(super.isStillValid()) {
            return this.getBlockPosition(this.target).equals(this.positionTarget);
        }
        return false;
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        this.target = null;
    }
}
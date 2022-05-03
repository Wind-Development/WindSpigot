package ga.windpvp.windspigot.async.pathsearch.cache;
import net.minecraft.server.EntityInsentient;
import net.minecraft.server.MathHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PathEntity;
import net.minecraft.server.PathPoint;

//This is based on Minetick's async path searching
public class SearchCacheEntry {
    protected long tick;
    
    int startX;
    int startY;
    int startZ;
    
    int targetX;
    int targetY;
    int targetZ;
    
    protected EntityInsentient entity;
    private PathEntity path;

    public SearchCacheEntry(EntityInsentient entity, PathEntity path) {
        this.entity = entity;
        
        this.startX = MathHelper.floor(this.entity.locX);
        this.startY = MathHelper.floor(this.entity.locY) + 1;
        this.startZ = MathHelper.floor(this.entity.locZ);
        
        this.path = path;
        this.tick = this.getCurrentTick();
    }

    protected int getCurrentTick() {
        return MinecraftServer.getServer().at();
    }

    public boolean isStillValid() {
        return this.didSearchSucceed() && (this.getCurrentTick() - this.tick < 10);
    }

    public PathEntity getPathEntity() {
        return this.path;
    }

    public boolean hasExpired() {
        return !this.entity.isAlive() || !this.entity.valid  || (this.getCurrentTick() - this.tick) > 100;
    }

    public boolean didSearchSucceed() {
        return this.path != null;
    }

    public PathEntity getAdjustedPathEntity() {
        if(this.path != null && (this.path.e() < this.path.d() - 1)) {
            PathPoint pathpoint = this.path.a(this.path.e());
            double currentDist = this.entity.e(pathpoint.a, pathpoint.b, pathpoint.c);
            while(this.path.e() < this.path.d() - 1) {
                pathpoint = this.path.a(this.path.e() + 1);
                double nextDist = this.entity.e(pathpoint.a, pathpoint.b, pathpoint.c);
                if(nextDist < currentDist) {
                    currentDist = nextDist;
                    this.path.a();
                } else {
                    break;
                }
            }
        }
        return this.path;
    }

    public void cleanup() {
        this.entity = null;
        this.path = null;
    }
}
package ga.windpvp.windspigot.async.pathsearch.cache;
import net.minecraft.server.BlockPosition;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityInsentient;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PathEntity;
import net.minecraft.server.PathPoint;

//This is based on Minetick's async path searching
public class SearchCacheEntry {
    protected long tick;
    protected BlockPosition positionStart;
    protected BlockPosition positionTarget;
    protected EntityInsentient entity;
    private PathEntity path;

    public SearchCacheEntry(EntityInsentient entity, PathEntity path) {
        this.entity = entity;
        this.positionStart = this.getBlockPosition(this.entity);
        this.path = path;
        this.tick = this.getCurrentTick();
    }

    protected int getCurrentTick() {
        return MinecraftServer.getServer().at();
    }

    protected BlockPosition getBlockPosition(Entity entity) {
        return new BlockPosition(entity).up();
    }

    protected BlockPosition getBlockPosition(int x, int y, int z) {
        return new BlockPosition(x, y, z);
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
        this.positionStart = null;
        this.positionTarget = null;
        this.entity = null;
        this.path = null;
    }
}
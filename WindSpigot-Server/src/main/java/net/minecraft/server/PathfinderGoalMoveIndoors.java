package net.minecraft.server;

import ga.windpvp.windspigot.async.pathsearch.position.PositionPathSearchType;

public class PathfinderGoalMoveIndoors extends PathfinderGoal {
	private EntityCreature a;
	private VillageDoor b;
	private int c = -1;
	private int d = -1;

	public PathfinderGoalMoveIndoors(EntityCreature var1) {
		this.a = var1;
		this.a(1);
	}

	public boolean a() {
		BlockPosition var1 = new BlockPosition(this.a);
		if ((!this.a.world.w() || this.a.world.S() && !this.a.world.getBiome(var1).e())
				&& !this.a.world.worldProvider.o()) {
			if (this.a.bc().nextInt(50) != 0) {
				return false;
			} else if (this.c != -1 && this.a.e((double) this.c, this.a.locY, (double) this.d) < 4.0D) {
				return false;
			} else {
				Village var2 = this.a.world.ae().getClosestVillage(var1, 14);
				if (var2 == null) {
					return false;
				} else {
					this.b = var2.c(var1);
					return this.b != null;
				}
			}
		} else {
			return false;
		}
	}

	public boolean b() {
		return !this.a.getNavigation().m();
	}

	public void c() {
		this.c = -1;
		BlockPosition var1 = this.b.e();
		int var2 = var1.getX();
		int var3 = var1.getY();
		int var4 = var1.getZ();
		if (this.a.b(var1) > 256.0D) {
			Vec3D var5 = RandomPositionGenerator.a(this.a, 14, 3,
					new Vec3D((double) var2 + 0.5D, (double) var3, (double) var4 + 0.5D));
			if (var5 != null) {
				this.a.getNavigation().a(var5.a, var5.b, var5.c, 1.0D, PositionPathSearchType.MOVEINDOORS); // MinetickMod
			}
		} else {
			this.a.getNavigation().a((double) var2 + 0.5D, (double) var3, (double) var4 + 0.5D, 1.0D,
					PositionPathSearchType.MOVEINDOORS); // MinetickMod
		}

	}

	public void d() {
		this.c = this.b.e().getX();
		this.d = this.b.e().getZ();
		this.b = null;
	}
}
package net.minecraft.server;

import ga.windpvp.windspigot.async.pathsearch.position.PositionPathSearchType;

public abstract class PathfinderGoalGotoTarget extends PathfinderGoal {
	private final EntityCreature c;
	private final double d;
	protected int a;
	private int e;
	private int f;
	protected BlockPosition b;
	private boolean g;
	private int h;

	public PathfinderGoalGotoTarget(EntityCreature var1, double var2, int var4) {
		this.b = BlockPosition.ZERO;
		this.c = var1;
		this.d = var2;
		this.h = var4;
		this.a(5);
	}

	public boolean a() {
		if (this.a > 0) {
			--this.a;
			return false;
		} else {
			this.a = 200 + this.c.bc().nextInt(200);
			return this.g();
		}
	}

	public boolean b() {
		return this.e >= -this.f && this.e <= 1200 && this.a(this.c.world, this.b);
	}

	public void c() {
		this.c.getNavigation().a((double) ((float) this.b.getX()) + 0.5D, (double) (this.b.getY() + 1),
				(double) ((float) this.b.getZ()) + 0.5D, this.d, PositionPathSearchType.GOTOTARGET); // MinetickMod

		this.e = 0;
		this.f = this.c.bc().nextInt(this.c.bc().nextInt(1200) + 1200) + 1200;
	}

	public void d() {
	}

	public void e() {
		if (this.c.c(this.b.up()) > 1.0D) {
			this.g = false;
			++this.e;
			if (this.e % 40 == 0) {
				this.c.getNavigation().a((double) ((float) this.b.getX()) + 0.5D, (double) (this.b.getY() + 1),
						(double) ((float) this.b.getZ()) + 0.5D, this.d, PositionPathSearchType.GOTOTARGET); // MinetickMod

			}
		} else {
			this.g = true;
			--this.e;
		}

	}

	protected boolean f() {
		return this.g;
	}

	private boolean g() {
		int var1 = this.h;
		BlockPosition var3 = new BlockPosition(this.c);

		for (int var4 = 0; var4 <= 1; var4 = var4 > 0 ? -var4 : 1 - var4) {
			for (int var5 = 0; var5 < var1; ++var5) {
				for (int var6 = 0; var6 <= var5; var6 = var6 > 0 ? -var6 : 1 - var6) {
					for (int var7 = var6 < var5 && var6 > -var5 ? var5 : 0; var7 <= var5; var7 = var7 > 0 ? -var7
							: 1 - var7) {
						BlockPosition var8 = var3.a(var6, var4 - 1, var7);
						if (this.c.e(var8) && this.a(this.c.world, var8)) {
							this.b = var8;
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	protected abstract boolean a(World var1, BlockPosition var2);
}
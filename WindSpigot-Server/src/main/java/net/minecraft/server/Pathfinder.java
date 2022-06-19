package net.minecraft.server;

public class Pathfinder {
	private Path a = new Path();
	private PathPoint[] b = new PathPoint[32];
	private PathfinderAbstract c;

	public Pathfinder(PathfinderAbstract var1) {
		this.c = var1;
	}

	public PathEntity a(IBlockAccess var1, Entity var2, Entity var3, float var4) {
		return this.a(var1, var2, var3.locX, var3.getBoundingBox().b, var3.locZ, var4);
	}

	public PathEntity a(IBlockAccess var1, Entity var2, BlockPosition var3, float var4) {
		return this.a(var1, var2, (double) ((float) var3.getX() + 0.5F), (double) ((float) var3.getY() + 0.5F),
				(double) ((float) var3.getZ() + 0.5F), var4);
	}
	
	// WindSpigot start
	public PathEntity a(IBlockAccess var1, Entity var2, int x, int y, int z, float var4) {
		return this.a(var1, var2, (double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), var4);
	}
	// WindSpigot end

	private PathEntity a(IBlockAccess var1, Entity var2, double var3, double var5, double var7, float var9) {
		// WindSpigot - synchronize
		synchronized (var2) {
			this.a.a();
			this.c.a(var1, var2);
			PathPoint var10 = this.c.a(var2);
			PathPoint var11 = this.c.a(var2, var3, var5, var7);
			PathEntity var12 = this.a(var2, var10, var11, var9);
			this.c.a();
			return var12;
		}
	}

	private PathEntity a(Entity var1, PathPoint var2, PathPoint var3, float var4) {
		var2.e = 0.0F;
		var2.f = var2.b(var3);
		var2.g = var2.f;
		this.a.a();
		this.a.a(var2);
		PathPoint var5 = var2;

		while (!this.a.e()) {
			PathPoint var6 = this.a.c();
			if (var6.equals(var3)) {
				return this.a(var2, var3);
			}

			if (var6.b(var3) < var5.b(var3)) {
				var5 = var6;
			}

			var6.i = true;
			int var7 = this.c.a(this.b, var1, var6, var3, var4);

			for (int var8 = 0; var8 < var7; ++var8) {
				PathPoint var9 = this.b[var8];
				float var10 = var6.e + var6.b(var9);
				if (var10 < var4 * 2.0F && (!var9.a() || var10 < var9.e)) {
					var9.h = var6;
					var9.e = var10;
					var9.f = var9.b(var3);
					if (var9.a()) {
						this.a.a(var9, var9.e + var9.f);
					} else {
						var9.g = var9.e + var9.f;
						this.a.a(var9);
					}
				}
			}
		}

		if (var5 == var2) {
			return null;
		} else {
			return this.a(var2, var5);
		}
	}

	private PathEntity a(PathPoint var1, PathPoint var2) {
		int var3 = 1;

		PathPoint var4;
		for (var4 = var2; var4.h != null; var4 = var4.h) {
			++var3;
		}

		PathPoint[] var5 = new PathPoint[var3];
		var4 = var2;
		--var3;

		for (var5[var3] = var2; var4.h != null; var5[var3] = var4) {
			var4 = var4.h;
			--var3;
		}

		return new PathEntity(var5);
	}
}
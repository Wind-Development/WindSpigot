package net.minecraft.server;

import com.google.common.collect.Lists;

import ga.windpvp.windspigot.async.pathsearch.position.PositionPathSearchType;

import java.util.Iterator;
import java.util.List;

public class PathfinderGoalMoveThroughVillage extends PathfinderGoal {
	private EntityCreature a;
	private double b;
	private PathEntity c;
	private VillageDoor d;
	private boolean e;
	private List<VillageDoor> f = Lists.newArrayList();

	public PathfinderGoalMoveThroughVillage(EntityCreature var1, double var2, boolean var4) {
		this.a = var1;
		this.b = var2;
		this.e = var4;
		this.a(1);
		if (!(var1.getNavigation() instanceof Navigation)) {
			throw new IllegalArgumentException("Unsupported mob for MoveThroughVillageGoal");
		}
	}

	public boolean a() {
		this.f();
		if (this.e && this.a.world.w()) {
			return false;
		} else {
			Village var1 = this.a.world.ae().getClosestVillage(new BlockPosition(this.a), 0);
			if (var1 == null) {
				return false;
			} else {
				this.d = this.a(var1);
				if (this.d == null) {
					return false;
				} else {
					Navigation var2 = (Navigation) this.a.getNavigation();
					boolean var3 = var2.g();
					var2.b(false);
					this.c = var2.a(this.d.d());
					var2.b(var3);
					if (this.c != null) {
						return true;
					} else {
						Vec3D var4 = RandomPositionGenerator.a(this.a, 10, 7, new Vec3D((double) this.d.d().getX(),
								(double) this.d.d().getY(), (double) this.d.d().getZ()));
						if (var4 == null) {
							return false;
						} else {
							var2.b(false);
							this.c = this.a.getNavigation().a(var4.a, var4.b, var4.c, PositionPathSearchType.MOVETHROUGHVILLAGE); // MinetickMod
							var2.b(var3);
							return this.c != null;
						}
					}
				}
			}
		}
	}

	public boolean b() {
		if (this.a.getNavigation().m()) {
			return false;
		} else {
			float var1 = this.a.width + 4.0F;
			return this.a.b(this.d.d()) > (double) (var1 * var1);
		}
	}

	public void c() {
		this.a.getNavigation().a(this.c, this.b);
	}

	public void d() {
		if (this.a.getNavigation().m() || this.a.b(this.d.d()) < 16.0D) {
			this.f.add(this.d);
		}

	}

	private VillageDoor a(Village var1) {
		VillageDoor var2 = null;
		int var3 = Integer.MAX_VALUE;
		List var4 = var1.f();
		Iterator var5 = var4.iterator();

		while (var5.hasNext()) {
			VillageDoor var6 = (VillageDoor) var5.next();
			int var7 = var6.b(MathHelper.floor(this.a.locX), MathHelper.floor(this.a.locY),
					MathHelper.floor(this.a.locZ));
			if (var7 < var3 && !this.a(var6)) {
				var2 = var6;
				var3 = var7;
			}
		}

		return var2;
	}

	private boolean a(VillageDoor var1) {
		Iterator var2 = this.f.iterator();

		VillageDoor var3;
		do {
			if (!var2.hasNext()) {
				return false;
			}

			var3 = (VillageDoor) var2.next();
		} while (!var1.d().equals(var3.d()));

		return true;
	}

	private void f() {
		if (this.f.size() > 15) {
			this.f.remove(0);
		}

	}
}
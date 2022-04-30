package net.minecraft.server;

import java.util.Iterator;
import java.util.List;

import ga.windpvp.windspigot.async.pathsearch.position.PositionPathSearchType;

public class PathfinderGoalPlay extends PathfinderGoal {
	private EntityVillager a;
	private EntityLiving b;
	private double c;
	private int d;

	public PathfinderGoalPlay(EntityVillager var1, double var2) {
		this.a = var1;
		this.c = var2;
		this.a(1);
	}

	public boolean a() {
		if (this.a.getAge() >= 0) {
			return false;
		} else if (this.a.bc().nextInt(400) != 0) {
			return false;
		} else {
			List<EntityVillager> var1 = this.a.world.a(EntityVillager.class, this.a.getBoundingBox().grow(6.0D, 3.0D, 6.0D));
			double var2 = Double.MAX_VALUE;
			Iterator<EntityVillager> var4 = var1.iterator();

			while (var4.hasNext()) {
				EntityVillager var5 = (EntityVillager) var4.next();
				if (var5 != this.a && !var5.cn() && var5.getAge() < 0) {
					double var6 = var5.h(this.a);
					if (var6 <= var2) {
						var2 = var6;
						this.b = var5;
					}
				}
			}

			if (this.b == null) {
				Vec3D var8 = RandomPositionGenerator.a(this.a, 16, 3);
				if (var8 == null) {
					return false;
				}
			}

			return true;
		}
	}

	public boolean b() {
		return this.d > 0;
	}

	public void c() {
		if (this.b != null) {
			this.a.m(true);
		}

		this.d = 1000;
	}

	public void d() {
		this.a.m(false);
		this.b = null;
	}

	public void e() {
		--this.d;
		if (this.b != null) {
			if (this.a.h(this.b) > 4.0D) {
				this.a.getNavigation().a(this.b, this.c);
			}
		} else if (this.a.getNavigation().m()) {
			Vec3D var1 = RandomPositionGenerator.a(this.a, 16, 3);
			if (var1 == null) {
				return;
			}

			this.a.getNavigation().a(var1.a, var1.b, var1.c, this.c, PositionPathSearchType.PLAY); // MinetickMod
		}

	}
}
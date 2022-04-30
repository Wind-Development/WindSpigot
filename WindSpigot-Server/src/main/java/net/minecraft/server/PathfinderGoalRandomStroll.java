package net.minecraft.server;

import ga.windpvp.windspigot.async.pathsearch.position.PositionPathSearchType;

public class PathfinderGoalRandomStroll extends PathfinderGoal {
	private EntityCreature a;
	private double b;
	private double c;
	private double d;
	private double e;
	private int f;
	private boolean g;

	public PathfinderGoalRandomStroll(EntityCreature var1, double var2) {
		this(var1, var2, 120);
	}

	public PathfinderGoalRandomStroll(EntityCreature var1, double var2, int var4) {
		this.a = var1;
		this.e = var2;
		this.f = var4;
		this.a(1);
	}

	public boolean a() {
		if (!this.g) {
			if (this.a.bh() >= 100) {
				return false;
			}

			if (this.a.bc().nextInt(this.f) != 0) {
				return false;
			}
		}

		Vec3D var1 = RandomPositionGenerator.a(this.a, 10, 7);
		if (var1 == null) {
			return false;
		} else {
			this.b = var1.a;
			this.c = var1.b;
			this.d = var1.c;
			this.g = false;
			return true;
		}
	}

	public boolean b() {
		return !this.a.getNavigation().m();
	}

	public void c() {
		this.a.getNavigation().a(this.b, this.c, this.d, this.e, PositionPathSearchType.RANDOMSTROLL); // MinetickMod
	}

	public void f() {
		this.g = true;
	}

	public void setTimeBetweenMovement(int var1) {
		this.f = var1;
	}
}
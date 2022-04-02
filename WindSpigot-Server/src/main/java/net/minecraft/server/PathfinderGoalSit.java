package net.minecraft.server;

public class PathfinderGoalSit extends PathfinderGoal {

	private EntityTameableAnimal entity;
	private boolean willSit;

	public PathfinderGoalSit(EntityTameableAnimal entitytameableanimal) {
		this.entity = entitytameableanimal;
		this.a(5);
	}

	@Override
	public boolean a() {
		if (!this.entity.isTamed()) {
			return this.willSit && this.entity.getGoalTarget() == null; // CraftBukkit - Allow sitting for wild animals
		} else if (this.entity.V()) {
			return false;
		} else if (!this.entity.onGround) {
			return false;
		} else {
			EntityLiving entityliving = this.entity.getOwner();

			return entityliving == null || ((!(this.entity.h(entityliving) < 144.0D) || entityliving.getLastDamager() == null) && this.willSit);
		}
	}

	@Override
	public void c() {
		this.entity.getNavigation().n();
		this.entity.setSitting(true);
	}

	@Override
	public void d() {
		this.entity.setSitting(false);
	}

	public void setSitting(boolean flag) {
		this.willSit = flag;
	}
}

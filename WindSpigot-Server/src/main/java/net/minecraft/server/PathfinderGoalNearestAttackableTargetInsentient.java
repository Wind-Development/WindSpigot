package net.minecraft.server;

import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Predicate;

public class PathfinderGoalNearestAttackableTargetInsentient extends PathfinderGoal {

	private static final Logger a = LogManager.getLogger();
	private EntityInsentient b;
	private final Predicate<EntityLiving> c;
	private final PathfinderGoalNearestAttackableTarget.DistanceComparator d;
	private EntityLiving e;
	private Class<? extends EntityLiving> f;

	public PathfinderGoalNearestAttackableTargetInsentient(EntityInsentient entityinsentient,
			Class<? extends EntityLiving> oclass) {
		this.b = entityinsentient;
		this.f = oclass;
		if (entityinsentient instanceof EntityCreature) {
			PathfinderGoalNearestAttackableTargetInsentient.a
					.warn("Use NearestAttackableTargetGoal.class for PathfinerMob mobs!");
		}

		this.c = new Predicate() {
			public boolean a(EntityLiving entityliving) {
				double d0 = PathfinderGoalNearestAttackableTargetInsentient.this.f();

				if (entityliving.isSneaking()) {
					d0 *= 0.800000011920929D;
				}

				return entityliving.isInvisible() ? false
						: (entityliving.g(PathfinderGoalNearestAttackableTargetInsentient.this.b) > d0 ? false
								: PathfinderGoalTarget.a(PathfinderGoalNearestAttackableTargetInsentient.this.b,
										entityliving, false, true));
			}

			@Override
			public boolean apply(Object object) {
				return this.a((EntityLiving) object);
			}
		};
		this.d = new PathfinderGoalNearestAttackableTarget.DistanceComparator(entityinsentient);
	}

	@Override
	public boolean a() {
		double d0 = this.f();
		List list = this.b.world.a(this.f, this.b.getBoundingBox().grow(d0, 4.0D, d0), this.c);

		Collections.sort(list, this.d);
		if (list.isEmpty()) {
			return false;
		} else {
			this.e = (EntityLiving) list.get(0);
			return true;
		}
	}

	@Override
	public boolean b() {
		EntityLiving entityliving = this.b.getGoalTarget();

		if (entityliving == null) {
			return false;
		} else if (!entityliving.isAlive()) {
			return false;
		} else {
			double d0 = this.f();

			return this.b.h(entityliving) > d0 * d0 ? false
					: !(entityliving instanceof EntityPlayer)
							|| !((EntityPlayer) entityliving).playerInteractManager.isCreative();
		}
	}

	@Override
	public void c() {
		this.b.setGoalTarget(this.e, org.bukkit.event.entity.EntityTargetEvent.TargetReason.CLOSEST_ENTITY, true); // CraftBukkit
																													// -
																													// reason
		super.c();
	}

	@Override
	public void d() {
		this.b.setGoalTarget(null);
		super.c();
	}

	protected double f() {
		AttributeInstance attributeinstance = this.b.getAttributeInstance(GenericAttributes.FOLLOW_RANGE);

		return attributeinstance == null ? 16.0D : attributeinstance.getValue();
	}
}

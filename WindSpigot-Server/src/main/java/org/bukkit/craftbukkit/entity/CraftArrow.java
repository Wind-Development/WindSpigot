package org.bukkit.craftbukkit.entity;

import org.apache.commons.lang.Validate;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.projectiles.ProjectileSource;

import net.minecraft.server.EntityArrow;

public class CraftArrow extends AbstractProjectile implements Arrow {

	public CraftArrow(CraftServer server, EntityArrow entity) {
		super(server, entity);
	}

	@Override
	public void setKnockbackStrength(int knockbackStrength) {
		Validate.isTrue(knockbackStrength >= 0, "Knockback cannot be negative");
		getHandle().setKnockbackStrength(knockbackStrength);
	}

	@Override
	public int getKnockbackStrength() {
		return getHandle().knockbackStrength;
	}

	@Override
	public boolean isCritical() {
		return getHandle().isCritical();
	}

	@Override
	public void setCritical(boolean critical) {
		getHandle().setCritical(critical);
	}

	@Override
	public ProjectileSource getShooter() {
		return getHandle().projectileSource;
	}

	@Override
	public void setShooter(ProjectileSource shooter) {
		if (shooter instanceof LivingEntity) {
			getHandle().shooter = ((CraftLivingEntity) shooter).getHandle();
		} else {
			getHandle().shooter = null;
		}
		getHandle().projectileSource = shooter;
	}

	@Override
	public EntityArrow getHandle() {
		return (EntityArrow) entity;
	}

	@Override
	public String toString() {
		return "CraftArrow";
	}

	@Override
	public EntityType getType() {
		return EntityType.ARROW;
	}

	@Override
	@Deprecated
	public LivingEntity _INVALID_getShooter() {
		if (getHandle().shooter == null) {
			return null;
		}
		return (LivingEntity) getHandle().shooter.getBukkitEntity();
	}

	@Override
	@Deprecated
	public void _INVALID_setShooter(LivingEntity shooter) {
		getHandle().shooter = ((CraftLivingEntity) shooter).getHandle();
	}

	// Spigot start
	private final Arrow.Spigot spigot = new Arrow.Spigot() {
		@Override
		public double getDamage() {
			return getHandle().j();
		}

		@Override
		public void setDamage(double damage) {
			getHandle().b(damage);
		}
	};

	@Override
	public Arrow.Spigot spigot() {
		return spigot;
	}
	// Spigot end
}

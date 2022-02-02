package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ThrownExpBottle;

import net.minecraft.server.EntityThrownExpBottle;

public class CraftThrownExpBottle extends CraftProjectile implements ThrownExpBottle {
	public CraftThrownExpBottle(CraftServer server, EntityThrownExpBottle entity) {
		super(server, entity);
	}

	@Override
	public EntityThrownExpBottle getHandle() {
		return (EntityThrownExpBottle) entity;
	}

	@Override
	public String toString() {
		return "EntityThrownExpBottle";
	}

	@Override
	public EntityType getType() {
		return EntityType.THROWN_EXP_BOTTLE;
	}
}

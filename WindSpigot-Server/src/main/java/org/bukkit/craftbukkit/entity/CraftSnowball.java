package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Snowball;

import net.minecraft.server.EntitySnowball;

public class CraftSnowball extends CraftProjectile implements Snowball {
	public CraftSnowball(CraftServer server, EntitySnowball entity) {
		super(server, entity);
	}

	@Override
	public EntitySnowball getHandle() {
		return (EntitySnowball) entity;
	}

	@Override
	public String toString() {
		return "CraftSnowball";
	}

	@Override
	public EntityType getType() {
		return EntityType.SNOWBALL;
	}
}

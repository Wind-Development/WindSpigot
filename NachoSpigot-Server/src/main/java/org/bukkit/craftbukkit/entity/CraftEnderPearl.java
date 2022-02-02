package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.EntityType;

import net.minecraft.server.EntityEnderPearl;

public class CraftEnderPearl extends CraftProjectile implements EnderPearl {
	public CraftEnderPearl(CraftServer server, EntityEnderPearl entity) {
		super(server, entity);
	}

	@Override
	public EntityEnderPearl getHandle() {
		return (EntityEnderPearl) entity;
	}

	@Override
	public String toString() {
		return "CraftEnderPearl";
	}

	@Override
	public EntityType getType() {
		return EntityType.ENDER_PEARL;
	}
}

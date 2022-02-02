package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ghast;

import net.minecraft.server.EntityGhast;

public class CraftGhast extends CraftFlying implements Ghast {

	public CraftGhast(CraftServer server, EntityGhast entity) {
		super(server, entity);
	}

	@Override
	public EntityGhast getHandle() {
		return (EntityGhast) entity;
	}

	@Override
	public String toString() {
		return "CraftGhast";
	}

	@Override
	public EntityType getType() {
		return EntityType.GHAST;
	}
}

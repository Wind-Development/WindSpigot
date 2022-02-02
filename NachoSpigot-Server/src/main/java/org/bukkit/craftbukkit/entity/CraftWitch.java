package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Witch;

import net.minecraft.server.EntityWitch;

public class CraftWitch extends CraftMonster implements Witch {
	public CraftWitch(CraftServer server, EntityWitch entity) {
		super(server, entity);
	}

	@Override
	public EntityWitch getHandle() {
		return (EntityWitch) entity;
	}

	@Override
	public String toString() {
		return "CraftWitch";
	}

	@Override
	public EntityType getType() {
		return EntityType.WITCH;
	}
}

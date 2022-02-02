package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wither;

import net.minecraft.server.EntityWither;

public class CraftWither extends CraftMonster implements Wither {
	public CraftWither(CraftServer server, EntityWither entity) {
		super(server, entity);
	}

	@Override
	public EntityWither getHandle() {
		return (EntityWither) entity;
	}

	@Override
	public String toString() {
		return "CraftWither";
	}

	@Override
	public EntityType getType() {
		return EntityType.WITHER;
	}
}

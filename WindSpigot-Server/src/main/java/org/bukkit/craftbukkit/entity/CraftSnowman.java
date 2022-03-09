package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Snowman;

import net.minecraft.server.EntitySnowman;

public class CraftSnowman extends CraftGolem implements Snowman {
	public CraftSnowman(CraftServer server, EntitySnowman entity) {
		super(server, entity);
	}

	@Override
	public EntitySnowman getHandle() {
		return (EntitySnowman) entity;
	}

	@Override
	public String toString() {
		return "CraftSnowman";
	}

	@Override
	public EntityType getType() {
		return EntityType.SNOWMAN;
	}
}

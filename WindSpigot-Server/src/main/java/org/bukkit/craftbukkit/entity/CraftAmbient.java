package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Ambient;
import org.bukkit.entity.EntityType;

import net.minecraft.server.EntityAmbient;

public class CraftAmbient extends CraftLivingEntity implements Ambient {
	public CraftAmbient(CraftServer server, EntityAmbient entity) {
		super(server, entity);
	}

	@Override
	public EntityAmbient getHandle() {
		return (EntityAmbient) entity;
	}

	@Override
	public String toString() {
		return "CraftAmbient";
	}

	@Override
	public EntityType getType() {
		return EntityType.UNKNOWN;
	}
}

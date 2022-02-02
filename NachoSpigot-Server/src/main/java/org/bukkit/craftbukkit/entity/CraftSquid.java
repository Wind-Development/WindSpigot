package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Squid;

import net.minecraft.server.EntitySquid;

public class CraftSquid extends CraftWaterMob implements Squid {

	public CraftSquid(CraftServer server, EntitySquid entity) {
		super(server, entity);
	}

	@Override
	public EntitySquid getHandle() {
		return (EntitySquid) entity;
	}

	@Override
	public String toString() {
		return "CraftSquid";
	}

	@Override
	public EntityType getType() {
		return EntityType.SQUID;
	}
}

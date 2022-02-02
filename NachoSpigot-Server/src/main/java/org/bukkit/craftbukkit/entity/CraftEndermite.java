package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Endermite;
import org.bukkit.entity.EntityType;

import net.minecraft.server.EntityEndermite;

public class CraftEndermite extends CraftMonster implements Endermite {

	public CraftEndermite(CraftServer server, EntityEndermite entity) {
		super(server, entity);
	}

	@Override
	public String toString() {
		return "CraftEndermite";
	}

	@Override
	public EntityType getType() {
		return EntityType.ENDERMITE;
	}
}

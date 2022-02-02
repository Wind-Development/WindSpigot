package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LeashHitch;

import net.minecraft.server.EntityLeash;

public class CraftLeash extends CraftHanging implements LeashHitch {
	public CraftLeash(CraftServer server, EntityLeash entity) {
		super(server, entity);
	}

	@Override
	public EntityLeash getHandle() {
		return (EntityLeash) entity;
	}

	@Override
	public String toString() {
		return "CraftLeash";
	}

	@Override
	public EntityType getType() {
		return EntityType.LEASH_HITCH;
	}
}

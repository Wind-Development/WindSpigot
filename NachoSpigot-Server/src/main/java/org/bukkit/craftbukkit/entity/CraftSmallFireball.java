package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.SmallFireball;

import net.minecraft.server.EntitySmallFireball;

public class CraftSmallFireball extends CraftFireball implements SmallFireball {
	public CraftSmallFireball(CraftServer server, EntitySmallFireball entity) {
		super(server, entity);
	}

	@Override
	public EntitySmallFireball getHandle() {
		return (EntitySmallFireball) entity;
	}

	@Override
	public String toString() {
		return "CraftSmallFireball";
	}

	@Override
	public EntityType getType() {
		return EntityType.SMALL_FIREBALL;
	}
}

package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.MushroomCow;

import net.minecraft.server.EntityMushroomCow;

public class CraftMushroomCow extends CraftCow implements MushroomCow {
	public CraftMushroomCow(CraftServer server, EntityMushroomCow entity) {
		super(server, entity);
	}

	@Override
	public EntityMushroomCow getHandle() {
		return (EntityMushroomCow) entity;
	}

	@Override
	public String toString() {
		return "CraftMushroomCow";
	}

	@Override
	public EntityType getType() {
		return EntityType.MUSHROOM_COW;
	}
}

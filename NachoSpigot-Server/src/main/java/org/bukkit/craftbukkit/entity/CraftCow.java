package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Cow;
import org.bukkit.entity.EntityType;

import net.minecraft.server.EntityCow;

public class CraftCow extends CraftAnimals implements Cow {

	public CraftCow(CraftServer server, EntityCow entity) {
		super(server, entity);
	}

	@Override
	public EntityCow getHandle() {
		return (EntityCow) entity;
	}

	@Override
	public String toString() {
		return "CraftCow";
	}

	@Override
	public EntityType getType() {
		return EntityType.COW;
	}
}

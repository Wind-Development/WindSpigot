package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Silverfish;

import net.minecraft.server.EntitySilverfish;

public class CraftSilverfish extends CraftMonster implements Silverfish {
	public CraftSilverfish(CraftServer server, EntitySilverfish entity) {
		super(server, entity);
	}

	@Override
	public EntitySilverfish getHandle() {
		return (EntitySilverfish) entity;
	}

	@Override
	public String toString() {
		return "CraftSilverfish";
	}

	@Override
	public EntityType getType() {
		return EntityType.SILVERFISH;
	}
}

package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.EntityType;

import net.minecraft.server.EntityCaveSpider;

public class CraftCaveSpider extends CraftSpider implements CaveSpider {
	public CraftCaveSpider(CraftServer server, EntityCaveSpider entity) {
		super(server, entity);
	}

	@Override
	public EntityCaveSpider getHandle() {
		return (EntityCaveSpider) entity;
	}

	@Override
	public String toString() {
		return "CraftCaveSpider";
	}

	@Override
	public EntityType getType() {
		return EntityType.CAVE_SPIDER;
	}
}

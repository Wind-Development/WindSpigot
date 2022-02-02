package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.EntityType;

import net.minecraft.server.EntityChicken;

public class CraftChicken extends CraftAnimals implements Chicken {

	public CraftChicken(CraftServer server, EntityChicken entity) {
		super(server, entity);
	}

	@Override
	public EntityChicken getHandle() {
		return (EntityChicken) entity;
	}

	@Override
	public String toString() {
		return "CraftChicken";
	}

	@Override
	public EntityType getType() {
		return EntityType.CHICKEN;
	}
}

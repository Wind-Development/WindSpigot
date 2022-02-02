package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Animals;

import net.minecraft.server.EntityAnimal;

public class CraftAnimals extends CraftAgeable implements Animals {

	public CraftAnimals(CraftServer server, EntityAnimal entity) {
		super(server, entity);
	}

	@Override
	public EntityAnimal getHandle() {
		return (EntityAnimal) entity;
	}

	@Override
	public String toString() {
		return "CraftAnimals";
	}
}

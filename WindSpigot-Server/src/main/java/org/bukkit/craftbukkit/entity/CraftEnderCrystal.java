package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EntityType;

import net.minecraft.server.EntityEnderCrystal;

public class CraftEnderCrystal extends CraftEntity implements EnderCrystal {
	public CraftEnderCrystal(CraftServer server, EntityEnderCrystal entity) {
		super(server, entity);
	}

	@Override
	public EntityEnderCrystal getHandle() {
		return (EntityEnderCrystal) entity;
	}

	@Override
	public String toString() {
		return "CraftEnderCrystal";
	}

	@Override
	public EntityType getType() {
		return EntityType.ENDER_CRYSTAL;
	}
}

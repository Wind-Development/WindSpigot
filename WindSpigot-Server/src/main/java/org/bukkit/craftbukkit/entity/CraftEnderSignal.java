package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EnderSignal;
import org.bukkit.entity.EntityType;

import net.minecraft.server.EntityEnderSignal;

public class CraftEnderSignal extends CraftEntity implements EnderSignal {
	public CraftEnderSignal(CraftServer server, EntityEnderSignal entity) {
		super(server, entity);
	}

	@Override
	public EntityEnderSignal getHandle() {
		return (EntityEnderSignal) entity;
	}

	@Override
	public String toString() {
		return "CraftEnderSignal";
	}

	@Override
	public EntityType getType() {
		return EntityType.ENDER_SIGNAL;
	}
}
package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.ComplexLivingEntity;

import net.minecraft.server.EntityLiving;

public abstract class CraftComplexLivingEntity extends CraftLivingEntity implements ComplexLivingEntity {
	public CraftComplexLivingEntity(CraftServer server, EntityLiving entity) {
		super(server, entity);
	}

	@Override
	public EntityLiving getHandle() {
		return (EntityLiving) entity;
	}

	@Override
	public String toString() {
		return "CraftComplexLivingEntity";
	}
}

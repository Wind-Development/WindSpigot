package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.WaterMob;

import net.minecraft.server.EntityWaterAnimal;

public class CraftWaterMob extends CraftLivingEntity implements WaterMob
{

	public CraftWaterMob(CraftServer server, EntityWaterAnimal entity)
	{
		super(server, entity);
	}

	@Override
	public EntityWaterAnimal getHandle()
	{
		return (EntityWaterAnimal) entity;
	}

	@Override
	public String toString()
	{
		return "CraftWaterMob";
	}
}
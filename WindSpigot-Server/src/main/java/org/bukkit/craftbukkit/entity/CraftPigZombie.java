package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PigZombie;

import net.minecraft.server.EntityPigZombie;

public class CraftPigZombie extends CraftZombie implements PigZombie {

	public CraftPigZombie(CraftServer server, EntityPigZombie entity) {
		super(server, entity);
	}

	@Override
	public int getAnger() {
		return getHandle().angerLevel;
	}

	@Override
	public void setAnger(int level) {
		getHandle().angerLevel = level;
	}

	@Override
	public void setAngry(boolean angry) {
		setAnger(angry ? 400 : 0);
	}

	@Override
	public boolean isAngry() {
		return getAnger() > 0;
	}

	@Override
	public EntityPigZombie getHandle() {
		return (EntityPigZombie) entity;
	}

	@Override
	public String toString() {
		return "CraftPigZombie";
	}

	@Override
	public EntityType getType() {
		return EntityType.PIG_ZOMBIE;
	}
}

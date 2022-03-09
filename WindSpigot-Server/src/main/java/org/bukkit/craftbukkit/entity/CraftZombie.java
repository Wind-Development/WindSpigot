package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;

import net.minecraft.server.EntityZombie;

public class CraftZombie extends CraftMonster implements Zombie {

	public CraftZombie(CraftServer server, EntityZombie entity) {
		super(server, entity);
	}

	@Override
	public EntityZombie getHandle() {
		return (EntityZombie) entity;
	}

	@Override
	public String toString() {
		return "CraftZombie";
	}

	@Override
	public EntityType getType() {
		return EntityType.ZOMBIE;
	}

	@Override
	public boolean isBaby() {
		return getHandle().isBaby();
	}

	@Override
	public void setBaby(boolean flag) {
		getHandle().setBaby(flag);
	}

	@Override
	public boolean isVillager() {
		return getHandle().isVillager();
	}

	@Override
	public void setVillager(boolean flag) {
		getHandle().setVillager(flag);
	}
}

package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;

import net.minecraft.server.EntityLiving;
import net.minecraft.server.EntityTNTPrimed;

public class CraftTNTPrimed extends CraftEntity implements TNTPrimed {

	public CraftTNTPrimed(CraftServer server, EntityTNTPrimed entity) {
		super(server, entity);
	}

	@Override
	public float getYield() {
		return getHandle().yield;
	}

	@Override
	public boolean isIncendiary() {
		return getHandle().isIncendiary;
	}

	@Override
	public void setIsIncendiary(boolean isIncendiary) {
		getHandle().isIncendiary = isIncendiary;
	}

	@Override
	public void setYield(float yield) {
		getHandle().yield = yield;
	}

	@Override
	public int getFuseTicks() {
		return getHandle().fuseTicks;
	}

	@Override
	public void setFuseTicks(int fuseTicks) {
		getHandle().fuseTicks = fuseTicks;
	}

	@Override
	public EntityTNTPrimed getHandle() {
		return (EntityTNTPrimed) entity;
	}

	@Override
	public String toString() {
		return "CraftTNTPrimed";
	}

	@Override
	public EntityType getType() {
		return EntityType.PRIMED_TNT;
	}

	@Override
	public Entity getSource() {
		EntityLiving source = getHandle().getSource();

		if (source != null) {
			Entity bukkitEntity = source.getBukkitEntity();

			if (bukkitEntity.isValid()) {
				return bukkitEntity;
			}
		}

		return null;
	}

	// PaperSpigot start
	@Override
	public org.bukkit.Location getSourceLoc() {
		return getHandle().sourceLoc;
	}
	// PaperSpigot end
}

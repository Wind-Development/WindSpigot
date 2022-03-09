package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.entity.Boat;
import org.bukkit.entity.EntityType;

import net.minecraft.server.EntityBoat;

public class CraftBoat extends CraftVehicle implements Boat {

	public CraftBoat(CraftServer server, EntityBoat entity) {
		super(server, entity);
	}

	@Override
	public double getMaxSpeed() {
		return getHandle().maxSpeed;
	}

	@Override
	public void setMaxSpeed(double speed) {
		if (speed >= 0D) {
			getHandle().maxSpeed = speed;
		}
	}

	@Override
	public double getOccupiedDeceleration() {
		return getHandle().occupiedDeceleration;
	}

	@Override
	public void setOccupiedDeceleration(double speed) {
		if (speed >= 0D) {
			getHandle().occupiedDeceleration = speed;
		}
	}

	@Override
	public double getUnoccupiedDeceleration() {
		return getHandle().unoccupiedDeceleration;
	}

	@Override
	public void setUnoccupiedDeceleration(double speed) {
		getHandle().unoccupiedDeceleration = speed;
	}

	@Override
	public boolean getWorkOnLand() {
		return getHandle().landBoats;
	}

	@Override
	public void setWorkOnLand(boolean workOnLand) {
		getHandle().landBoats = workOnLand;
	}

	@Override
	public EntityBoat getHandle() {
		return (EntityBoat) entity;
	}

	@Override
	public String toString() {
		return "CraftBoat";
	}

	@Override
	public EntityType getType() {
		return EntityType.BOAT;
	}
}

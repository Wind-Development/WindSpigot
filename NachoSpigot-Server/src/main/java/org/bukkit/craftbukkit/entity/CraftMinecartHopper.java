package org.bukkit.craftbukkit.entity;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.inventory.Inventory;

import net.minecraft.server.EntityMinecartHopper;

final class CraftMinecartHopper extends CraftMinecart implements HopperMinecart {
	private final CraftInventory inventory;

	CraftMinecartHopper(CraftServer server, EntityMinecartHopper entity) {
		super(server, entity);
		inventory = new CraftInventory(entity);
	}

	@Override
	public String toString() {
		return "CraftMinecartHopper{" + "inventory=" + inventory + '}';
	}

	@Override
	public EntityType getType() {
		return EntityType.MINECART_HOPPER;
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}
}

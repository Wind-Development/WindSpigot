package org.bukkit.craftbukkit.inventory;

import org.bukkit.inventory.BeaconInventory;
import org.bukkit.inventory.ItemStack;

import net.minecraft.server.TileEntityBeacon;

public class CraftInventoryBeacon extends CraftInventory implements BeaconInventory {
	public CraftInventoryBeacon(TileEntityBeacon beacon) {
		super(beacon);
	}

	@Override
	public void setItem(ItemStack item) {
		setItem(0, item);
	}

	@Override
	public ItemStack getItem() {
		return getItem(0);
	}
}

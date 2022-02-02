package org.bukkit.craftbukkit.inventory;

import org.bukkit.inventory.MerchantInventory;

import net.minecraft.server.InventoryMerchant;

public class CraftInventoryMerchant extends CraftInventory implements MerchantInventory {
	public CraftInventoryMerchant(InventoryMerchant merchant) {
		super(merchant);
	}
}

package org.bukkit.craftbukkit.entity;

import org.apache.commons.lang.Validate;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import net.minecraft.server.EntityVillager;

public class CraftVillager extends CraftAgeable implements Villager, InventoryHolder {
	public CraftVillager(CraftServer server, EntityVillager entity) {
		super(server, entity);
	}

	@Override
	public EntityVillager getHandle() {
		return (EntityVillager) entity;
	}

	@Override
	public String toString() {
		return "CraftVillager";
	}

	@Override
	public EntityType getType() {
		return EntityType.VILLAGER;
	}

	@Override
	public Profession getProfession() {
		return Profession.getProfession(getHandle().getProfession());
	}

	@Override
	public void setProfession(Profession profession) {
		Validate.notNull(profession);
		getHandle().setProfession(profession.getId());
	}

	@Override
	public Inventory getInventory() {
		return new CraftInventory(getHandle().inventory);
	}
}

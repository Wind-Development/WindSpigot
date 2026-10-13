package org.bukkit.craftbukkit.entity;

import java.util.Collection;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.windpvp.windspigot.potion.ThrownPotionEffects;

import net.minecraft.server.EntityPotion;

public class CraftThrownPotion extends CraftProjectile implements ThrownPotion {
	public CraftThrownPotion(CraftServer server, EntityPotion entity) {
		super(server, entity);
	}

	@Override
	public Collection<PotionEffect> getEffects() {
		// Ensure the item stack exists (same side-effect as getItem()).
		getHandle().getPotionValue();
		// Include CustomPotionEffects NBT — matches EntityPotion splash via Items.POTION.h
		return ThrownPotionEffects.getEffects(getHandle().item);
	}

	@Override
	public ItemStack getItem() {
		// We run this method once since it will set the item stack if there is none.
		getHandle().getPotionValue();

		return CraftItemStack.asBukkitCopy(getHandle().item);
	}

	@Override
	public void setItem(ItemStack item) {
		// The ItemStack must not be null.
		Validate.notNull(item, "ItemStack cannot be null.");

		// The ItemStack must be a potion.
		Validate.isTrue(item.getType() == Material.POTION,
				"ItemStack must be a potion. This item stack was " + item.getType() + ".");

		getHandle().item = CraftItemStack.asNMSCopy(item);
	}

	@Override
	public EntityPotion getHandle() {
		return (EntityPotion) entity;
	}

	@Override
	public String toString() {
		return "CraftThrownPotion";
	}

	@Override
	public EntityType getType() {
		return EntityType.SPLASH_POTION;
	}
}

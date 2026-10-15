package org.bukkit.craftbukkit.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.google.common.collect.ImmutableList;

import net.minecraft.server.EntityPotion;
import net.minecraft.server.MobEffect;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.PotionBrewer;

public class CraftThrownPotion extends CraftProjectile implements ThrownPotion {
	public CraftThrownPotion(CraftServer server, EntityPotion entity) {
		super(server, entity);
	}

	@Override
	public Collection<PotionEffect> getEffects() {
		// Ensure the item stack exists (same side-effect as getItem()).
		getHandle().getPotionValue();
		// WindSpigot - include CustomPotionEffects (matches ItemPotion.h / EntityPotion splash)
		net.minecraft.server.ItemStack item = getHandle().item;
		if (item == null) {
			return ImmutableList.of();
		}
		NBTTagCompound tag = item.hasTag() ? item.getTag() : null;
		List<MobEffect> list;
		if (tag != null && tag.hasKeyOfType("CustomPotionEffects", 9)) {
			list = new ArrayList<MobEffect>();
			NBTTagList nbtList = tag.getList("CustomPotionEffects", 10);
			for (int i = 0; i < nbtList.size(); i++) {
				MobEffect effect = MobEffect.b(nbtList.get(i));
				if (effect != null) {
					list.add(effect);
				}
			}
		} else {
			@SuppressWarnings("unchecked")
			List<MobEffect> fromDamage = PotionBrewer.getEffects(item.getData(), false);
			list = fromDamage;
		}
		if (list == null || list.isEmpty()) {
			return ImmutableList.of();
		}
		ImmutableList.Builder<PotionEffect> builder = ImmutableList.builder();
		for (MobEffect effect : list) {
			if (effect == null) {
				continue;
			}
			PotionEffectType type = PotionEffectType.getById(effect.getEffectId());
			if (type == null) {
				continue;
			}
			builder.add(new PotionEffect(type, effect.getDuration(), effect.getAmplifier(), effect.isAmbient(),
					effect.isShowParticles()));
		}
		return builder.build();
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

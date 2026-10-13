package com.windpvp.windspigot.potion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.google.common.collect.ImmutableList;

import net.minecraft.server.ItemStack;
import net.minecraft.server.MobEffect;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.PotionBrewer;

/**
 * Resolves splash-potion effects the same way {@code EntityPotion} / {@code ItemPotion#h}
 * does: {@code CustomPotionEffects} NBT replaces damage-value effects (1.8 semantics).
 */
public final class ThrownPotionEffects {

	private ThrownPotionEffects() {
	}

	/**
	 * Effects that would be applied when this potion item splashes.
	 *
	 * @param item NMS potion stack (may be null)
	 * @return immutable Bukkit effects; never null
	 */
	public static Collection<PotionEffect> getEffects(ItemStack item) {
		if (item == null) {
			return ImmutableList.of();
		}
		NBTTagCompound tag = item.hasTag() ? item.getTag() : null;
		return getEffects(tag, item.getData());
	}

	/**
	 * Resolve effects from optional item NBT and potion damage value.
	 * <p>
	 * Mirrors {@code ItemPotion#h(ItemStack)} without requiring a live {@link ItemStack}:
	 * if {@code CustomPotionEffects} is present, those effects are used exclusively
	 * (even when the list is empty); otherwise damage-value effects from
	 * {@link PotionBrewer} are used.
	 *
	 * @param tag    item NBT (may be null)
	 * @param damage potion damage / data value
	 * @return immutable Bukkit effects; never null
	 */
	public static Collection<PotionEffect> getEffects(NBTTagCompound tag, int damage) {
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
			List<MobEffect> fromDamage = PotionBrewer.getEffects(damage, false);
			list = fromDamage;
		}
		return toBukkit(list);
	}

	/**
	 * Convert NMS mob effects to Bukkit potion effects.
	 *
	 * @param list NMS effects (may be null)
	 * @return immutable Bukkit effects; never null
	 */
	public static Collection<PotionEffect> toBukkit(List<MobEffect> list) {
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
}

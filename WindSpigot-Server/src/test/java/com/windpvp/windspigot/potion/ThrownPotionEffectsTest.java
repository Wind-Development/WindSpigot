package com.windpvp.windspigot.potion;

import java.util.Collection;
import java.util.Collections;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import net.minecraft.server.MobEffect;
import net.minecraft.server.MobEffectList;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.NBTTagList;
import net.minecraft.server.PotionBrewer;

/**
 * Verifies thrown-potion effect resolution includes CustomPotionEffects NBT
 * (same rules as ItemPotion.h / EntityPotion splash).
 */
public class ThrownPotionEffectsTest {

	@BeforeClass
	public static void registerPotionEffectTypes() {
		// Force CraftBukkit registration of PotionEffectType from NMS MobEffectList.
		MobEffectList.FASTER_MOVEMENT.getClass();
		Assert.assertNotNull("SPEED potion effect type must be registered", PotionEffectType.getById(1));
	}

	private static NBTTagCompound effectTag(int id, int amplifier, int duration, boolean ambient, boolean particles) {
		NBTTagCompound effect = new NBTTagCompound();
		effect.setByte("Id", (byte) id);
		effect.setByte("Amplifier", (byte) amplifier);
		effect.setInt("Duration", duration);
		effect.setBoolean("Ambient", ambient);
		effect.setBoolean("ShowParticles", particles);
		return effect;
	}

	private static NBTTagCompound tagWithCustomEffects(NBTTagCompound... effects) {
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagList list = new NBTTagList();
		for (NBTTagCompound effect : effects) {
			list.add(effect);
		}
		tag.set("CustomPotionEffects", list);
		return tag;
	}

	@Test
	public void customNbtEffectsAreReturned() {
		// Splash harming damage value — custom NBT must win over vanilla effects.
		NBTTagCompound tag = tagWithCustomEffects(effectTag(1, 1, 200, false, true),
				effectTag(5, 0, 100, true, false));

		Collection<PotionEffect> effects = ThrownPotionEffects.getEffects(tag, 16460);
		Assert.assertEquals(2, effects.size());

		boolean foundSpeed = false;
		boolean foundStrength = false;
		for (PotionEffect effect : effects) {
			if (effect.getType().getId() == 1) {
				foundSpeed = true;
				Assert.assertEquals(1, effect.getAmplifier());
				Assert.assertEquals(200, effect.getDuration());
				Assert.assertFalse(effect.isAmbient());
				Assert.assertTrue(effect.hasParticles());
			} else if (effect.getType().getId() == 5) {
				foundStrength = true;
				Assert.assertEquals(0, effect.getAmplifier());
				Assert.assertEquals(100, effect.getDuration());
				Assert.assertTrue(effect.isAmbient());
				Assert.assertFalse(effect.hasParticles());
			}
		}
		Assert.assertTrue("expected SPEED custom effect", foundSpeed);
		Assert.assertTrue("expected INCREASE_DAMAGE custom effect", foundStrength);
	}

	@Test
	public void customNbtReplacesDamageValueEffects() {
		Collection<PotionEffect> vanillaEffects = ThrownPotionEffects.getEffects((NBTTagCompound) null, 16460);
		Assert.assertFalse("vanilla harming potion should have damage-value effects", vanillaEffects.isEmpty());

		NBTTagCompound tag = tagWithCustomEffects(effectTag(10, 0, 40, false, true)); // REGENERATION only
		Collection<PotionEffect> customEffects = ThrownPotionEffects.getEffects(tag, 16460);
		Assert.assertEquals(1, customEffects.size());
		PotionEffect only = customEffects.iterator().next();
		Assert.assertEquals(10, only.getType().getId());
		Assert.assertEquals(40, only.getDuration());

		for (PotionEffect effect : customEffects) {
			Assert.assertNotEquals("HARM from damage value must not appear when CustomPotionEffects present", 7,
					effect.getType().getId());
		}
	}

	@Test
	public void emptyCustomListDoesNotFallThroughToDamageValue() {
		// ItemPotion.h: presence of CustomPotionEffects list uses that list even if empty.
		Collection<PotionEffect> effects = ThrownPotionEffects.getEffects(tagWithCustomEffects(), 16460);
		Assert.assertTrue(effects.isEmpty());
	}

	@Test
	public void nullItemReturnsEmpty() {
		Assert.assertTrue(ThrownPotionEffects.getEffects(null).isEmpty());
	}

	@Test
	public void damageValueEffectsWithoutCustomNbt() {
		Collection<PotionEffect> effects = ThrownPotionEffects.getEffects((NBTTagCompound) null, 8193);
		Assert.assertFalse(effects.isEmpty());

		@SuppressWarnings("unchecked")
		java.util.List<MobEffect> expected = PotionBrewer.getEffects(8193, false);
		Assert.assertNotNull(expected);
		Assert.assertEquals(ThrownPotionEffects.toBukkit(expected).size(), effects.size());
	}

	@Test
	public void toBukkitPreservesFields() {
		MobEffect nms = new MobEffect(8, 120, 2, true, false); // JUMP
		Collection<PotionEffect> effects = ThrownPotionEffects.toBukkit(Collections.singletonList(nms));
		Assert.assertEquals(1, effects.size());
		PotionEffect bukkit = effects.iterator().next();
		Assert.assertEquals(8, bukkit.getType().getId());
		Assert.assertEquals(120, bukkit.getDuration());
		Assert.assertEquals(2, bukkit.getAmplifier());
		Assert.assertTrue(bukkit.isAmbient());
		Assert.assertFalse(bukkit.hasParticles());
	}

	@Test
	public void tagWithoutCustomKeyUsesDamageValue() {
		NBTTagCompound unrelated = new NBTTagCompound();
		unrelated.setString("display", "nope");
		Collection<PotionEffect> withTag = ThrownPotionEffects.getEffects(unrelated, 8193);
		Collection<PotionEffect> withoutTag = ThrownPotionEffects.getEffects((NBTTagCompound) null, 8193);
		Assert.assertEquals(withoutTag.size(), withTag.size());
	}
}

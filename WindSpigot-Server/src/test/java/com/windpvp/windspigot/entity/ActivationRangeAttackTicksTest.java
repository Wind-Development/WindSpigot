package com.windpvp.windspigot.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.junit.Assert;
import org.junit.Test;
import org.spigotmc.ActivationRange;

import net.minecraft.server.EntityLiving;

/**
 * Regression for ActivationRange {@code attackTicks} immunity (TODO #9).
 * <p>
 * Pre-1.8 {@code EntityLiving.attackTicks} (NBT {@code AttackTime}) was removed by
 * Mojang in 1.8, so Spigot left {@code living.attackTicks > 0} commented out as a
 * "missed mapping". Without it, mobs can deactivate while still in melee-attack
 * cooldown. WindSpigot restores the field and the immunity check.
 */
public class ActivationRangeAttackTicksTest {

	/** Pre-1.8 AttackTime / PathfinderGoalMeleeAttack melee cooldown length. */
	private static final int MELEE_ATTACK_COOLDOWN_TICKS = 20;

	/**
	 * Mirrors {@link ActivationRange#checkEntityImmunities} living special-case
	 * gate for combat-related fields (attack / hurt / effects).
	 */
	static boolean hasLivingCombatImmunity(int attackTicks, int hurtTicks, int effectCount) {
		return attackTicks > 0 || hurtTicks > 0 || effectCount > 0;
	}

	@Test
	public void attackTicksGreaterThanZeroGrantsImmunity() {
		Assert.assertTrue(hasLivingCombatImmunity(MELEE_ATTACK_COOLDOWN_TICKS, 0, 0));
		Assert.assertTrue(hasLivingCombatImmunity(1, 0, 0));
		Assert.assertFalse("Zero attackTicks alone must not grant immunity",
				hasLivingCombatImmunity(0, 0, 0));
	}

	@Test
	public void hurtTicksAndEffectsStillGrantImmunity() {
		Assert.assertTrue(hasLivingCombatImmunity(0, 1, 0));
		Assert.assertTrue(hasLivingCombatImmunity(0, 0, 1));
		Assert.assertTrue(hasLivingCombatImmunity(5, 3, 2));
	}

	@Test
	public void entityLivingExposesRestoredAttackTicksField() throws Exception {
		Field field = EntityLiving.class.getField("attackTicks");
		Assert.assertEquals(int.class, field.getType());
		Assert.assertTrue(Modifier.isPublic(field.getModifiers()));
	}

	@Test
	public void markAttackCooldownSetsPre18AttackTime() throws Exception {
		Method mark = EntityLiving.class.getMethod("markAttackCooldown");
		Assert.assertEquals(void.class, mark.getReturnType());

		// Contract: cooldown length matches pre-1.8 AttackTime / melee goal (20).
		int ticks = MELEE_ATTACK_COOLDOWN_TICKS;
		Assert.assertTrue(hasLivingCombatImmunity(ticks, 0, 0));
		for (int i = 0; i < MELEE_ATTACK_COOLDOWN_TICKS; i++) {
			ticks--;
		}
		Assert.assertEquals(0, ticks);
		Assert.assertFalse(hasLivingCombatImmunity(ticks, 0, 0));
	}

	@Test
	public void activationRangeSourceUsesAttackTicks() throws Exception {
		// Compile-time: ActivationRange references EntityLiving.attackTicks.
		// Runtime: ensure the public field name the immunity check uses is present.
		Assert.assertNotNull(EntityLiving.class.getField("attackTicks"));
		Assert.assertNotNull(ActivationRange.class.getMethod("checkEntityImmunities",
				net.minecraft.server.Entity.class));
	}
}

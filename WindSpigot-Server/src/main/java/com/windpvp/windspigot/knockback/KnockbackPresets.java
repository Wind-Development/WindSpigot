package com.windpvp.windspigot.knockback;

import dev.cobblesword.nachospigot.knockback.KnockbackProfile;

/**
 * Built-in knockback preset values.
 * <p>
 * Seeded into {@code knockback.yml} only when a profile name is missing (existing
 * operator edits are never overwritten).
 */
public final class KnockbackPresets {

	private KnockbackPresets() {
	}

	/**
	 * WindSpigot PvP profile ({@code windpvp}).
	 * <p>
	 * Kohi-style melee with slightly stronger projectile and punch values. Every
	 * knob is set explicitly so the seed does not rely on
	 * {@link CraftKnockbackProfile} field defaults.
	 */
	public static void applyWindpvp(KnockbackProfile profile) {
		profile.setStopSprint(true);

		profile.setFrictionHorizontal(2.0D);
		profile.setFrictionVertical(2.0D);

		profile.setHorizontal(0.35D);
		profile.setVertical(0.35D);
		profile.setVerticalMax(0.4D);
		profile.setVerticalMin(-1.0D);

		profile.setExtraHorizontal(0.425D);
		profile.setExtraVertical(0.085D);
		profile.setWTapExtraHorizontal(0.425D);
		profile.setWTapExtraVertical(0.085D);

		profile.setAddHorizontal(0.0D);
		profile.setAddVertical(0.0D);

		// Projectiles stronger than melee; pearl stays at melee so ender-pearl
		// hits do not fling harder than a normal punch.
		profile.setRodHorizontal(0.425D);
		profile.setRodVertical(0.425D);
		profile.setArrowHorizontal(0.425D);
		profile.setArrowVertical(0.425D);
		profile.setPearlHorizontal(0.35D);
		profile.setPearlVertical(0.35D);
		profile.setSnowballHorizontal(0.425D);
		profile.setSnowballVertical(0.425D);
		profile.setEggHorizontal(0.425D);
		profile.setEggVertical(0.425D);
	}

	/**
	 * Kohi / Nacho-compatible default used when {@code knockback.current} is unset.
	 */
	public static void applyKohi(KnockbackProfile profile) {
		profile.setStopSprint(true);

		profile.setFrictionHorizontal(2.0D);
		profile.setFrictionVertical(2.0D);

		profile.setHorizontal(0.35D);
		profile.setVertical(0.35D);
		profile.setVerticalMax(0.4D);
		profile.setVerticalMin(-1.0D);

		profile.setExtraHorizontal(0.425D);
		profile.setExtraVertical(0.085D);
		profile.setWTapExtraHorizontal(0.425D);
		profile.setWTapExtraVertical(0.085D);

		profile.setAddHorizontal(0.0D);
		profile.setAddVertical(0.0D);

		profile.setRodHorizontal(0.35D);
		profile.setRodVertical(0.35D);
		profile.setArrowHorizontal(0.35D);
		profile.setArrowVertical(0.35D);
		profile.setPearlHorizontal(0.35D);
		profile.setPearlVertical(0.35D);
		profile.setSnowballHorizontal(0.35D);
		profile.setSnowballVertical(0.35D);
		profile.setEggHorizontal(0.35D);
		profile.setEggVertical(0.35D);
	}
}

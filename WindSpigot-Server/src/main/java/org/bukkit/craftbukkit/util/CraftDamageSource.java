package org.bukkit.craftbukkit.util;

import net.minecraft.server.DamageSource;

// Util class to create custom DamageSources.
public final class CraftDamageSource extends DamageSource {

	public static DamageSource copyOf(final DamageSource original) {
		CraftDamageSource newSource = new CraftDamageSource(original.translationIndex);

		// Check ignoresArmor
		if (original.ignoresArmor()) {
			newSource.setIgnoreArmor();
		}

		// Check magic
		if (original.isMagic()) {
			newSource.setMagic();
		}

		// Check fire
		if (original.isExplosion()) {
			newSource.setExplosion();
		}

		return newSource;
	}

	public static DAMAGE_SOURCE getSource(String value) {
		return DAMAGE_SOURCE.getSource(value);
	}

	private CraftDamageSource(String identifier) {
		super(identifier);
	}

	public enum DAMAGE_SOURCE {
		FIRE("inFire"),
		LIGHTNING("lightningBolt"),
		BURN("onFire"),
		LAVA("lava"),
		STUCK("inWall"),
		DROWN("drown"),
		STARVE("starve"),
		MELTING("MELTING"),
		POISON("POISON"),
		CACTUS("cactus"),
		OUT_OF_WORLD("outOfWorld"),
		GENERIC("generic"),
		MAGIC("magic"),
		WITHER("wither"),
		ANVIL("anvil"),
		FALLING_BLOCK("fallingBlock"),
		FALL("fall");

		private String code;
		DAMAGE_SOURCE(String code) {
			this.code = code;
		}

		public static DAMAGE_SOURCE getSource(String value) {
			for (DAMAGE_SOURCE l : DAMAGE_SOURCE.values()) {
				if (l.code.equalsIgnoreCase(value)) return l;
			}

			throw new IllegalArgumentException("Leg not found. Amputated?");
		}

	}
}

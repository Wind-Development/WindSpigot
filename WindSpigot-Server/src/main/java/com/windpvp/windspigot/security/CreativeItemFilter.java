package com.windpvp.windspigot.security;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;

/**
 * Creative-slot filter for {@code PacketPlayInSetCreativeSlot}.
 * <p>
 * CraftBukkit historically maintained a hardcoded numeric ID blacklist with
 * {@code // TODO: Check after every update}. That set drifted across 1.8
 * revisions (missing wood doors, banners, comparators, etc.), allowing
 * modified clients to spawn technical / non-inventory block forms.
 * <p>
 * This filter is derived from {@link Material} so new block materials that
 * follow Bukkit's {@code NAME}/{@code NAME_ITEM} or on/off / wall-standing
 * patterns are included automatically, with an explicit set only for
 * technical blocks that have no such naming relationship.
 */
public final class CreativeItemFilter {

	private static final Set<Integer> FORBIDDEN_IDS = buildForbiddenIds();

	private CreativeItemFilter() {
	}

	/**
	 * @return unmodifiable set of numeric item/block IDs rejected when
	 *         {@code settings.filter-creative-items} is enabled
	 */
	public static Set<Integer> forbiddenIds() {
		return FORBIDDEN_IDS;
	}

	/**
	 * @param itemId legacy numeric ID from {@code Item.getId(...)}
	 * @return true if creative inventory packets must not spawn this ID
	 */
	public static boolean isForbidden(int itemId) {
		return FORBIDDEN_IDS.contains(itemId);
	}

	static Set<Integer> buildForbiddenIds() {
		Set<Integer> ids = new HashSet<Integer>();

		// Block materials with a distinct inventory item named NAME_ITEM
		// (flower pot, skull, wood doors, brewing stand, …).
		for (Material material : Material.values()) {
			if (!material.isBlock() || material == Material.AIR) {
				continue;
			}
			Material itemForm = Material.getMaterial(material.name() + "_ITEM");
			if (itemForm != null && itemForm.getId() != material.getId()) {
				ids.add(Integer.valueOf(material.getId()));
			}
		}

		// Wall / standing block states that share a single inventory item.
		add(ids, Material.SIGN_POST, Material.WALL_SIGN);
		add(ids, Material.STANDING_BANNER, Material.WALL_BANNER);

		// Powered / unpowered block states; inventory item is the separate
		// diode / comparator item (or the "on" torch).
		add(ids, Material.DIODE_BLOCK_OFF, Material.DIODE_BLOCK_ON);
		add(ids, Material.REDSTONE_COMPARATOR_OFF, Material.REDSTONE_COMPARATOR_ON);
		add(ids, Material.REDSTONE_TORCH_OFF);

		// Double-slab block forms (inventory item is the single slab).
		add(ids, Material.DOUBLE_STEP, Material.WOOD_DOUBLE_STEP, Material.DOUBLE_STONE_SLAB2);

		// Technical blocks and block forms with renamed inventory items
		// (BED_BLOCK→BED, WOODEN_DOOR→WOOD_DOOR, …) that do not follow
		// the NAME_ITEM pattern.
		add(ids, Material.WATER, Material.STATIONARY_WATER, Material.LAVA, Material.STATIONARY_LAVA,
				Material.BED_BLOCK, Material.PISTON_EXTENSION, Material.PISTON_MOVING_PIECE, Material.FIRE,
				Material.MOB_SPAWNER, Material.REDSTONE_WIRE, Material.CROPS, Material.SOIL,
				Material.BURNING_FURNACE, Material.WOODEN_DOOR, Material.IRON_DOOR_BLOCK,
				Material.GLOWING_REDSTONE_ORE, Material.SUGAR_CANE_BLOCK, Material.PORTAL, Material.CAKE_BLOCK,
				Material.PUMPKIN_STEM, Material.MELON_STEM, Material.NETHER_WARTS, Material.ENDER_PORTAL,
				Material.COCOA, Material.TRIPWIRE, Material.DAYLIGHT_DETECTOR_INVERTED);

		return Collections.unmodifiableSet(ids);
	}

	private static void add(Set<Integer> ids, Material... materials) {
		for (Material material : materials) {
			if (material != null) {
				ids.add(Integer.valueOf(material.getId()));
			}
		}
	}
}

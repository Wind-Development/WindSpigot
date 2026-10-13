package com.windpvp.windspigot.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.junit.Test;

/**
 * CreativeItemFilterTest: Material-derived creative-slot blacklist covers the
 * legacy CraftBukkit IDs and 1.8 additions the hardcoded list missed.
 */
public class CreativeItemFilterTest {

	/** Original CraftBukkit numeric blacklist (pre-Material derivation). */
	private static final Set<Integer> LEGACY_IDS = new HashSet<Integer>(Arrays.asList(8, 9, 10, 11, 26, 34, 36, 43, 51,
			52, 55, 59, 60, 62, 63, 64, 68, 71, 74, 75, 83, 90, 92, 93, 94, 104, 105, 115, 117, 118, 119, 125, 127, 132,
			140, 141, 142, 144));

	@Test
	public void retainsEveryLegacyCraftBukkitId() {
		for (Integer id : LEGACY_IDS) {
			assertTrue("legacy creative-forbidden id must remain filtered: " + id,
					CreativeItemFilter.isForbidden(id.intValue()));
		}
	}

	@Test
	public void coversEighteenProtocolDriftMaterials() {
		// These existed in 1.8 Material but were absent from the hardcoded HashSet.
		assertForbidden(Material.REDSTONE_COMPARATOR_OFF);
		assertForbidden(Material.REDSTONE_COMPARATOR_ON);
		assertForbidden(Material.STANDING_BANNER);
		assertForbidden(Material.WALL_BANNER);
		assertForbidden(Material.DAYLIGHT_DETECTOR_INVERTED);
		assertForbidden(Material.DOUBLE_STONE_SLAB2);
		assertForbidden(Material.SPRUCE_DOOR);
		assertForbidden(Material.BIRCH_DOOR);
		assertForbidden(Material.JUNGLE_DOOR);
		assertForbidden(Material.ACACIA_DOOR);
		assertForbidden(Material.DARK_OAK_DOOR);
	}

	@Test
	public void allowsNormalPlaceableBlocksAndItems() {
		assertAllowed(Material.STONE);
		assertAllowed(Material.DIRT);
		assertAllowed(Material.GRASS);
		assertAllowed(Material.TORCH);
		assertAllowed(Material.CHEST);
		assertAllowed(Material.FURNACE);
		assertAllowed(Material.REDSTONE_TORCH_ON);
		assertAllowed(Material.STEP);
		assertAllowed(Material.WOOD_STEP);
		assertAllowed(Material.STONE_SLAB2);
		assertAllowed(Material.DAYLIGHT_DETECTOR);
		assertAllowed(Material.BARRIER);
		assertAllowed(Material.DIAMOND_SWORD);
		assertAllowed(Material.WOOD_DOOR);
		assertAllowed(Material.BED);
		assertAllowed(Material.BANNER);
		assertAllowed(Material.SPRUCE_DOOR_ITEM);
		assertAllowed(Material.REDSTONE_COMPARATOR);
	}

	@Test
	public void nameItemPatternCatchesInventoryDualForms() {
		assertForbidden(Material.FLOWER_POT);
		assertForbidden(Material.SKULL);
		assertForbidden(Material.BREWING_STAND);
		assertForbidden(Material.CAULDRON);
		assertAllowed(Material.FLOWER_POT_ITEM);
		assertAllowed(Material.SKULL_ITEM);
		assertAllowed(Material.BREWING_STAND_ITEM);
		assertAllowed(Material.CAULDRON_ITEM);
	}

	@Test
	public void forbiddenSetIsUnmodifiable() {
		try {
			CreativeItemFilter.forbiddenIds().add(Integer.valueOf(1));
			org.junit.Assert.fail("forbiddenIds must be unmodifiable");
		} catch (UnsupportedOperationException expected) {
			// ok
		}
	}

	private static void assertForbidden(Material material) {
		assertTrue(material.name() + " (" + material.getId() + ") must be creative-forbidden",
				CreativeItemFilter.isForbidden(material.getId()));
	}

	private static void assertAllowed(Material material) {
		assertFalse(material.name() + " (" + material.getId() + ") must remain creatively spawnable",
				CreativeItemFilter.isForbidden(material.getId()));
	}
}

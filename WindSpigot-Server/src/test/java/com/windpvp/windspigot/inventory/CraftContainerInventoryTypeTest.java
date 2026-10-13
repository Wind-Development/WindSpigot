package com.windpvp.windspigot.inventory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.bukkit.craftbukkit.inventory.CraftContainer;
import org.bukkit.event.inventory.InventoryType;
import org.junit.Test;

/**
 * Invalid InventoryTypes (player CRAFTING / CREATIVE) must fail loudly in
 * CraftContainer instead of silent fall-through / wrong notch window type.
 */
public class CraftContainerInventoryTypeTest {

	@Test
	public void creativeTypeThrowsFromGetNotchInventoryType() {
		assertThrowsCantOpen(InventoryType.CREATIVE);
	}

	@Test
	public void craftingTypeThrowsFromGetNotchInventoryType() {
		assertThrowsCantOpen(InventoryType.CRAFTING);
	}

	@Test
	public void workbenchRemainsValidCraftingTableWindow() {
		assertEquals("minecraft:crafting_table", CraftContainer.getNotchInventoryType(InventoryType.WORKBENCH));
	}

	@Test
	public void legitimateTypesStillMap() {
		assertEquals("minecraft:furnace", CraftContainer.getNotchInventoryType(InventoryType.FURNACE));
		assertEquals("minecraft:chest", CraftContainer.getNotchInventoryType(InventoryType.CHEST));
		assertEquals("minecraft:chest", CraftContainer.getNotchInventoryType(InventoryType.PLAYER));
		assertEquals("minecraft:chest", CraftContainer.getNotchInventoryType(InventoryType.ENDER_CHEST));
		assertEquals("minecraft:hopper", CraftContainer.getNotchInventoryType(InventoryType.HOPPER));
		assertEquals("minecraft:anvil", CraftContainer.getNotchInventoryType(InventoryType.ANVIL));
		assertEquals("minecraft:beacon", CraftContainer.getNotchInventoryType(InventoryType.BEACON));
		assertEquals("minecraft:dispenser", CraftContainer.getNotchInventoryType(InventoryType.DISPENSER));
		assertEquals("minecraft:enchanting_table", CraftContainer.getNotchInventoryType(InventoryType.ENCHANTING));
		assertEquals("minecraft:brewing_stand", CraftContainer.getNotchInventoryType(InventoryType.BREWING));
	}

	private static void assertThrowsCantOpen(InventoryType type) {
		try {
			CraftContainer.getNotchInventoryType(type);
			fail("expected IllegalArgumentException for " + type);
		} catch (IllegalArgumentException expected) {
			assertEquals("Can't open a " + type + " inventory!", expected.getMessage());
		}
	}
}

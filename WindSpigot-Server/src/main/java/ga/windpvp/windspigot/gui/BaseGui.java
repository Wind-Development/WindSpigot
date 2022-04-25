package ga.windpvp.windspigot.gui;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import dev.cobblesword.nachospigot.commons.MCUtils;

public abstract class BaseGui {

	protected Inventory inventory;

	public BaseGui(String name) {
		MCUtils.ensureMain(() -> {
			inventory = Bukkit.createInventory(null, 36, name);
			initItems();
		}); // Run next tick, server might not be initialized
	}

	protected void initItems() {

	}
	
	public Inventory getInventory() {
		return this.inventory;
	}

}

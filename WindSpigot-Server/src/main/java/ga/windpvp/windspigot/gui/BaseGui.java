package ga.windpvp.windspigot.gui;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public abstract class BaseGui {

	protected Inventory inventory;

	public BaseGui(String name) {
		inventory = Bukkit.createInventory(null, 36, name);
		initItems();
	}

	protected void initItems() {

	}
	
	public Inventory getInventory() {
		return this.inventory;
	}

}

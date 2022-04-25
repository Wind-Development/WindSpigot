package ga.windpvp.windspigot.gui.admingui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import ga.windpvp.windspigot.gui.BaseGui;

public class AdminGui extends BaseGui {
	
	private ClickHandler handler;
	
	public AdminGui() {
		super("WindSpigot Admin GUI");
		handler = new ClickHandler();
	}

	@Override
	protected void initItems() {
		ItemStack glassBackground = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 3);
		
		glassBackground.getItemMeta().setDisplayName("");
		
		for (int slot = 0; slot < 36; ++slot) {
			inventory.setItem(slot, glassBackground);
		}
		
		ItemStack mobAiToggleButton = new ItemStack(Material.SKULL);
		mobAiToggleButton.getItemMeta().setDisplayName("§3Toggle Mob AI");
		inventory.setItem(10, glassBackground);
		
		ItemStack setMaxSlotsButton = new ItemStack(Material.CHEST);
		setMaxSlotsButton.getItemMeta().setDisplayName("§3Set maximum player slots");
		inventory.setItem(12, setMaxSlotsButton);

		ItemStack anticrashButton = new ItemStack(Material.BARRIER);
		anticrashButton.getItemMeta().setDisplayName("§3Toggle anticrash");
		inventory.setItem(14, anticrashButton);
		
		ItemStack serverPerformanceButton = new ItemStack(Material.REDSTONE);
		serverPerformanceButton.getItemMeta().setDisplayName("§3View server performance");
		inventory.setItem(16, serverPerformanceButton);
	}

	public ClickHandler getHandler() {
		return handler;
	}
	
}

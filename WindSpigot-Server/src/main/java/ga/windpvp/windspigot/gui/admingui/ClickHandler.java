package ga.windpvp.windspigot.gui.admingui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import dev.cobblesword.nachospigot.Nacho;
import dev.cobblesword.nachospigot.protocol.PacketListener;
import xyz.sculas.nacho.anticrash.AntiCrash;

public class ClickHandler {
	
	private void handleMobAi(Player player) {
		player.chat("/mobai");
	}
	
	private void handleMaxSlots(Player player) {
		player.chat("/sms "); // Incomplete
	}
	
	private void handleAnticrash() {
		
		
		for (PacketListener listener : Nacho.get().getPacketListeners()) {
			if (listener instanceof AntiCrash) {
				Nacho.get().unregisterPacketListener(listener);
				return;
			}
		}
		
		Nacho.get().registerPacketListener(new AntiCrash());
	}
	
	private void handleServerPerformance(Player player) {
		player.chat("/tps");
	}
	
	public void onClick(InventoryClickEvent e) {
		
		e.setCancelled(true);
				
		Player player = (Player) e.getWhoClicked();
		
		if (e.getSlot() == 10) {
			handleMobAi(player);
		}
		
		if (e.getSlot() == 12) {
			handleMaxSlots(player);
		}
		
		if (e.getSlot() == 14) {
			handleAnticrash();
		}
		
		if (e.getSlot() == 16) {
			handleServerPerformance(player);
		}
		
	}

}

package ga.windpvp.windspigot.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ga.windpvp.windspigot.config.WindSpigotConfig;

// Implements a commands that opens a gui
public class AdminGuiCommand extends Command {

	public AdminGuiCommand(String name) {
		super(name);
		this.description = "Opens the admin gui";
		this.usageMessage = "/admingui";
		this.setPermission("windspigot.command.admingui");
	}

	@Override
	public boolean execute(CommandSender sender, String currentAlias, String[] args) {
		if (!testPermission(sender)) {
			return true;
		}
		
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can use this command.");
			return true;
		}

		((Player) sender).openInventory(WindSpigotConfig.gui.getInventory());
		
		
		return true;
	}

}

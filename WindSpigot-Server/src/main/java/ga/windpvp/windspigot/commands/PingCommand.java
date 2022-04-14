package ga.windpvp.windspigot.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import ga.windpvp.windspigot.config.WindSpigotConfig;

// Implements a Mob AI toggle command
public class PingCommand extends Command {

	public PingCommand(String name) {
		super(name);
		this.description = "Shows a player's ping";
		this.usageMessage = "/ping";
		this.setPermission("windspigot.command.ping");
	}

	@Override
	public boolean execute(CommandSender sender, String currentAlias, String[] args) {
		if (!testPermission(sender)) {
			return true;
		}

		// If sender is a player send the player their own ping
		if ((args.length == 0) && sender instanceof Player) {
			String finalString = ChatColor.translateAlternateColorCodes('&', WindSpigotConfig.pingSelfCmdString)
					.replace("%ping%", ((Integer) ((CraftPlayer) sender).getPing()).toString());
			sender.sendMessage(finalString);
			
			// Otherwise send the ping of the argument player if valid
		} else if (args.length == 1) {
			
			Player pingPlayer = Bukkit.getPlayer(args[0]);
			if (pingPlayer != null && Bukkit.getOnlinePlayers().contains(pingPlayer)) {
				String finalString = ChatColor.translateAlternateColorCodes('&', WindSpigotConfig.pingOtherCmdString)
						.replace("%player%", pingPlayer.getName())
						.replace("%ping%", ((Integer) ((CraftPlayer) pingPlayer).getPing()).toString());
				sender.sendMessage(finalString);
			} else {
				sender.sendMessage(ChatColor.RED + "Invalid player!");
			}
			
			// Message on improper usage
		} else {
			sender.sendMessage(ChatColor.RED + "Usage: /ping <player>");
		}

		return true;
	}

}

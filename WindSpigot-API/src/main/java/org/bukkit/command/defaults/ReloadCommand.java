package org.bukkit.command.defaults;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class ReloadCommand extends BukkitCommand {
	public ReloadCommand(String name) {
		super(name);
		this.description = "Reloads the server configuration and plugins";
		this.usageMessage = "/reload";
		this.setPermission("bukkit.command.reload");
		this.setAliases(Collections.singletonList("rl"));
	}

	@Override
	public boolean execute(CommandSender sender, String currentAlias, String[] args) {
		if (!testPermission(sender))
			return true;

		Command.broadcastCommandMessage(sender, ChatColor.RED
				+ "Please note that this command is not supported and may cause issues when using some plugins.");
		Command.broadcastCommandMessage(sender,
				ChatColor.RED + "If you encounter any issues please use the /stop command to restart your server.");
		Bukkit.reload();
		Command.broadcastCommandMessage(sender, ChatColor.GREEN + "Reload complete.");

		return true;
	}

	// Spigot Start
	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args)
			throws IllegalArgumentException {
		return Collections.emptyList();
	}
	// Spigot End
}

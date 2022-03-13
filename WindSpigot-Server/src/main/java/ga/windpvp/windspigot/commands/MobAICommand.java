package ga.windpvp.windspigot.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import ga.windpvp.windspigot.internal.InternalAPI;

// Implements a Mob AI toggle command
public class MobAICommand extends Command {

	public MobAICommand(String name) {
		super(name);
		this.description = "Toggles Mob AI";
		this.usageMessage = "/mobai";
		this.setPermission("windspigot.command.mobai");
	}

	@Override
	public boolean execute(CommandSender sender, String currentAlias, String[] args) {
		if (!testPermission(sender)) {
			return true;
		}

		// WindSpigot - toggle mob ai
		sender.sendMessage(InternalAPI.toggleAi());

		return true;
	}

}

package ga.windpvp.windspigot.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldServer;

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

		// WindSpigot - Loop through each world and toggle mob ai
		String msg = null;
		boolean setMsg = true;
		for (WorldServer world : MinecraftServer.getServer().worlds) {
			if (world.nachoSpigotConfig.enableMobAI) {
				world.nachoSpigotConfig.enableMobAI = false;
				if (setMsg) {
					msg = ChatColor.AQUA + "Mob AI disabled in all worlds.";
					setMsg = false;
				}
			} else {
				world.nachoSpigotConfig.enableMobAI = true;
				if (setMsg) {
					msg = ChatColor.AQUA + "Mob AI enabled in all worlds.";
					setMsg = false;
				}
			}
		}
		sender.sendMessage(msg);

		return true;
	}

}

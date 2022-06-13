package ga.windpvp.windspigot.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldServer;

// Implements a Mob AI toggle command
public class MobAICommand extends Command {
	
	private boolean globalAI = true;

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

		globalAI = !globalAI;
		
		for (WorldServer world : MinecraftServer.getServer().worlds) {
			world.nachoSpigotConfig.enableMobAI = globalAI;
		}
		
		String status = globalAI ? "enabled" : "disabled";
		sender.sendMessage(ChatColor.GREEN + "Mob AI is now " + status + " in all worlds.");

		return true;
	}

}

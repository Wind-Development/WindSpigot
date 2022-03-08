package ga.windpvp.windspigot.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldServer;

// Implements a Mob AI toggle command
public class MobAICommand extends Command
{

	public MobAICommand(String name)
	{
		super(name);
		this.description = "Toggles Mob AI";
		this.usageMessage = "/mobai";
		this.setPermission("windspigot.command.mobai");
	}

	@Override
	public boolean execute(CommandSender sender, String currentAlias, String[] args)
	{
		if (!testPermission(sender))
		{
			return true;
		}
		
		// WindSpigot - Loop through each world and toggle mob ai
		for (WorldServer world : MinecraftServer.getServer().worlds)
		{
			if (world.nachoSpigotConfig.enableMobAI)
			{
				world.nachoSpigotConfig.enableMobAI = false;
			} else
			{
				world.nachoSpigotConfig.enableMobAI = true;
			}
		}
		
		return true;
	}

}

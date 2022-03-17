package ga.windpvp.windspigot;

import co.aikar.timings.Timings;
import ga.windpvp.windspigot.commands.MobAICommand;
import ga.windpvp.windspigot.config.WindSpigotConfig;
import net.minecraft.server.MinecraftServer;

public class WindSpigot {

	public WindSpigot() {
		this.init();
	}
	
	public void reload()
	{
		this.init();
	}

	private void initCmds() {
		// WindSpigot - mob ai cmd
		if (WindSpigotConfig.mobAiCmd) {
			MobAICommand mobAiCommand = new MobAICommand("mobai");
			MinecraftServer.getServer().server.getCommandMap().register(mobAiCommand.getName(), "", mobAiCommand);
		}
	}

	private void init() {
		initCmds();
		
		if (WindSpigotConfig.parallelWorld) {
			Timings.setTimingsEnabled(false);
			System.out.println(" ");
			System.out.println("Timings disabled due to parallel worlds enabled. Timings will break with parallel worlds.");
			System.out.println(" ");
		}
	}

}

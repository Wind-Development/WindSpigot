package ga.windpvp.windspigot;

import co.aikar.timings.Timings;
import ga.windpvp.windspigot.async.AsyncUtil;
import ga.windpvp.windspigot.async.world.TeleportSafety;
import ga.windpvp.windspigot.commands.MobAICommand;
import ga.windpvp.windspigot.config.WindSpigotConfig;
import ga.windpvp.windspigot.statistics.StatisticsClient;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;

import java.io.IOException;

public class WindSpigot {

	private StatisticsClient client;

	public WindSpigot() {
		this.init();
	}

	public void reload() {
		this.init();
	}

	private void initCmds() {
		// WindSpigot - mob ai cmd
		if (WindSpigotConfig.mobAiCmd) {
			MobAICommand mobAiCommand = new MobAICommand("mobai");
			MinecraftServer.getServer().server.getCommandMap().register(mobAiCommand.getName(), "", mobAiCommand);
		}
	}

	private void initStatistics() {
		Runnable runnable = (() -> {
			client = new StatisticsClient();
			try {
				if (!client.isConnected) {
					// Connect to the statistics server and notify that there is a new server
					client.start("150.230.35.78", 500);
					client.sendMessage("new server");
				}
			} catch (IOException ignored) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "There was an error enabling WindSpigot statistics! This is usually safe to ignore.");
			}
		});
		AsyncUtil.run(runnable);
	}

	private void init() {
		initCmds();
		initStatistics();

		if (WindSpigotConfig.parallelWorld) {
			Timings.setTimingsEnabled(false);
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Timings disabled due to parallel worlds enabled. Timings will break with parallel worlds.");
			TeleportSafety.init();
		}
		System.gc();
	}

	public StatisticsClient getClient() {
		return this.client;
	}
	
}

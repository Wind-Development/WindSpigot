package ga.windpvp.windspigot;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;

import co.aikar.timings.Timings;
import ga.windpvp.windspigot.async.AsyncUtil;
import ga.windpvp.windspigot.async.thread.HitDetection;
import ga.windpvp.windspigot.async.thread.Knockback;
import ga.windpvp.windspigot.async.world.TeleportSafety;
import ga.windpvp.windspigot.commands.MobAICommand;
import ga.windpvp.windspigot.config.WindSpigotConfig;
import ga.windpvp.windspigot.statistics.StatisticsClient;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.MinecraftServer;

public class WindSpigot {

	private StatisticsClient client;
	public static final Logger LOGGER = LogManager.getLogger(WindSpigot.class);
  
	public static HitDetection hitDetectionThread;
	public static Knockback knockbackThread;

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
		
		if (WindSpigotConfig.asyncHitDetection) {
            hitDetectionThread = new HitDetection("Hit Detection Thread");
			LOGGER.info("Successfully enabled async hit detection!");
        }
        if (WindSpigotConfig.asyncKnockback) {
            knockbackThread = new Knockback("Knockback Thread");
            LOGGER.info("Successfully enabled async knockback!");
        }
        
        System.setProperty( "io.netty.eventLoopThreads", Integer.toString(WindSpigotConfig.nettyThreads));


		if (WindSpigotConfig.parallelWorld) {
			Timings.setTimingsEnabled(false);
			LOGGER.info(" ");
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED
					+ "Timings disabled due to parallel worlds enabled. Timings will break with parallel worlds.");
			LOGGER.info(" ");
			TeleportSafety.init();
		}
		System.gc();
	}

	public StatisticsClient getClient() {
		return this.client;
	}
	
}

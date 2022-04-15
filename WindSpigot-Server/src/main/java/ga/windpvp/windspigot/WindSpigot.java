package ga.windpvp.windspigot;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;

import co.aikar.timings.Timings;
import ga.windpvp.windspigot.async.AsyncUtil;
import ga.windpvp.windspigot.async.thread.CombatThread;
import ga.windpvp.windspigot.async.world.TeleportRegistry;
import ga.windpvp.windspigot.commands.MobAICommand;
import ga.windpvp.windspigot.commands.PingCommand;
import ga.windpvp.windspigot.config.WindSpigotConfig;
import ga.windpvp.windspigot.statistics.StatisticsClient;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.MinecraftServer;

public class WindSpigot {

	private StatisticsClient client;
	public static final Logger LOGGER = LogManager.getLogger(WindSpigot.class);
  
	public static CombatThread hitDetectionThread;
	public static CombatThread knockbackThread;

	public WindSpigot() {
		this.init();
	}

	public void reload() {
		this.init();
	}

	private void initCmds() {
		if (WindSpigotConfig.mobAiCmd) {
			MobAICommand mobAiCommand = new MobAICommand("mobai");
			MinecraftServer.getServer().server.getCommandMap().register(mobAiCommand.getName(), "", mobAiCommand);
		}
		
		if (WindSpigotConfig.pingCmd) {
			PingCommand pingCommand = new PingCommand("ping");
			MinecraftServer.getServer().server.getCommandMap().register(pingCommand.getName(), "", pingCommand);
		}
	}

	private void initStatistics() {
		if (WindSpigotConfig.statistics) {
			Runnable statsRunnable = (() -> {
				client = new StatisticsClient();
				try {
					if (!client.isConnected) {
						// Connect to the statistics server and notify that there is a new server
						client.start("150.230.35.78", 500);
						client.sendMessage("new server");
						
						while (true) {
							try {
								// Keep alive, this tells the statistics server that this server
								// is still online
								client.sendMessage("keep alive packet");
								
								// Statistics are sent every 30 secs.
								TimeUnit.SECONDS.sleep(30);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						
					}
				} catch (IOException ignored) {
				}
			});
			AsyncUtil.run(statsRunnable);
		}
	}

	private void init() {
		initCmds();
		initStatistics();
		
		boolean hasConsoleSpace = false;
		
		if (WindSpigotConfig.asyncHitDetection) {
            LOGGER.info(" ");
            hitDetectionThread = new CombatThread("Hit Detection Thread");
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Successfully enabled async hit detection!");
        }
        if (WindSpigotConfig.asyncKnockback) {
            knockbackThread = new CombatThread("Knockback Thread");
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "Successfully enabled async knockback!");
            LOGGER.info(" ");
            hasConsoleSpace = true;
        }
        
        System.setProperty( "io.netty.eventLoopThreads", Integer.toString(WindSpigotConfig.nettyThreads));


		if (WindSpigotConfig.parallelWorld) {
			Timings.setTimingsEnabled(false);
			if (!hasConsoleSpace) {
				LOGGER.info(" ");
			}
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED
					+ "Timings disabled due to parallel worlds enabled. Timings will break with parallel worlds.");
			LOGGER.info(" ");
			TeleportRegistry.init();
		}
		System.gc();
	}

	public StatisticsClient getClient() {
		return this.client;
	}
	
}

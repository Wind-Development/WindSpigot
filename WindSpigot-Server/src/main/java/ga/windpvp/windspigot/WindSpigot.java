package ga.windpvp.windspigot;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import co.aikar.timings.Timings;
import ga.windpvp.windspigot.async.AsyncUtil;
import ga.windpvp.windspigot.async.pathsearch.SearchHandler;
import ga.windpvp.windspigot.async.thread.CombatThread;
import ga.windpvp.windspigot.commands.MobAICommand;
import ga.windpvp.windspigot.commands.PingCommand;
import ga.windpvp.windspigot.config.WindSpigotConfig;
import ga.windpvp.windspigot.hitdetection.LagCompensator;
import ga.windpvp.windspigot.statistics.StatisticsClient;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.MinecraftServer;

public class WindSpigot {

	private StatisticsClient client;
	
	public static final Logger LOGGER = LogManager.getLogger();
	private static final Logger DEBUG_LOGGER = LogManager.getLogger();
	private static WindSpigot INSTANCE;
	
	private CombatThread knockbackThread;
	
	private final Executor statisticsExecutor = Executors
			.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("WindSpigot Statistics Thread")
			.build());
	
	private volatile boolean statisticsEnabled = false;
	
	private SearchHandler searchHandler;
	private LagCompensator lagCompensator;

	public WindSpigot() {
		INSTANCE = this;
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
		if (WindSpigotConfig.statistics && !statisticsEnabled) {
			Runnable statisticsRunnable = (() -> {
				client = new StatisticsClient();
				try {
					statisticsEnabled = true;

					if (!client.isConnected) {
						// Connect to the statistics server and notify that there is a new server
						client.start("150.230.35.78", 500);
						client.sendMessage("new server");

						while (true) {
							// Keep alive, this tells the statistics server that this server
							// is still online
							client.sendMessage("keep alive packet");

							// Online players, this tells the statistics server how many players
							// are on
							client.sendMessage("player count packet " + Bukkit.getOnlinePlayers().size());

							// Statistics are sent every 40 secs.
							TimeUnit.SECONDS.sleep(40);
						}

					}
				} catch (Exception ignored) {}
			});
			AsyncUtil.run(statisticsRunnable, statisticsExecutor);
		}
	}

	private void init() {
		initCmds();
		initStatistics();
		
		// We do not want to initialize this again after a reload
		if (searchHandler == null && WindSpigotConfig.asyncPathSearches) {
			searchHandler = new SearchHandler();
		}
		
		if (WindSpigotConfig.asyncKnockback) {
			knockbackThread = new CombatThread("Knockback Thread");
		}
		if (WindSpigotConfig.parallelWorld) {
			LOGGER.info(" ");

			Timings.setTimingsEnabled(false);
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED
					+ "Timings have been disabled due to parallel worlds being enabled. Timings will break with parallel worlds.");
			
			LOGGER.info(" ");
		}
		if (WindSpigotConfig.improvedHitDetection) {
			lagCompensator = new LagCompensator();
		}
	}

	public StatisticsClient getClient() {
		return this.client;
	}
	
	public CombatThread getKnockbackThread() {
		return knockbackThread;
	}
	
    public LagCompensator getLagCompensator() {
        return lagCompensator;
    }
	
    public SearchHandler getSearchHandler() {
    	return searchHandler;
    }
    
	public static void debug(String msg) {
		if (WindSpigotConfig.debugMode)
			DEBUG_LOGGER.info(msg);
	}
	
	public static WindSpigot getInstance() {
		return INSTANCE;
	}
	
}

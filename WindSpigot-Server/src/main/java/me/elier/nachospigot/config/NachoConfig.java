package me.elier.nachospigot.config;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.sugarcanemc.sugarcane.util.yaml.YamlCommenter;

import com.google.common.base.Throwables;

import dev.cobblesword.nachospigot.OldNachoConfig;
import dev.cobblesword.nachospigot.commons.FileUtils;
import ga.windpvp.windspigot.WindSpigot;

public class NachoConfig {

	private static final Logger LOGGER = LogManager.getLogger(NachoConfig.class);
	private static File CONFIG_FILE;
	protected static final YamlCommenter c = new YamlCommenter();
	private static final String HEADER = "This is the main configuration file for NachoSpigot.\n"
			+ "As you can see, there's tons to configure. Some options may impact gameplay, so use\n"
			+ "with caution, and make sure you know what each option does before configuring.\n" + "\n"
			+ "If you need help with the configuration or have any questions related to NachoSpigot,\n"
			+ "join us in our Discord.\n" + "\n" + "Discord: https://discord.gg/SBTEbSx\n"
			+ "Github: https://github.com/CobbleSword/NachoSpigot\n";
	static YamlConfiguration config;
	static int version;

	public static void init(File configFile) {
		CONFIG_FILE = configFile;
		config = new YamlConfiguration();
		try {
			WindSpigot.LOGGER.info("Loading NachoSpigot config from " + configFile.getName());
			config.load(CONFIG_FILE);
		} catch (IOException ignored) {
		} catch (InvalidConfigurationException ex) {
			LOGGER.log(Level.ERROR, "Could not load nacho.yml, please correct your syntax errors", ex);
			throw Throwables.propagate(ex);
		}
		config.options().copyDefaults(true);
		File old_config = new File("nacho.json");
		if (old_config.exists()) {
			migrate(old_config);
		}

		int configVersion = 9; // Update this every new configuration update
		version = getInt("config-version", configVersion);
		set("config-version", configVersion);
		c.setHeader(HEADER);
		c.addComment("config-version", "Configuration version, do NOT modify this!");
		readConfig(NachoConfig.class, null);
	}

	private static void migrate(File old_config) {
		OldNachoConfig nachoJson = FileUtils.toObject(old_config, OldNachoConfig.class);
		if (nachoJson == null) {
			old_config.delete();
			return;
		}
		set("settings.save-empty-scoreboard-teams", nachoJson.saveEmptyScoreboardTeams);
		set("settings.commands.enable-version-command", nachoJson.enableVersionCommand);
		set("settings.commands.enable-plugins-command", nachoJson.enablePluginsCommand);
		set("settings.commands.enable-reload-command", nachoJson.enableReloadCommand);
		set("settings.fast-operators", nachoJson.useFastOperators);
		set("settings.patch-protocollib", nachoJson.patchProtocolLib);
		set("settings.stop-notify-bungee", nachoJson.stopNotifyBungee);
		set("settings.anti-malware", nachoJson.checkForMalware);
		set("settings.kick-on-illegal-behavior", nachoJson.kickOnIllegalBehavior);
		set("world-settings.default.tick-enchantment-tables", nachoJson.shouldTickEnchantmentTables);
		set("settings.panda-wire", nachoJson.usePandaWire);
		set("world-settings.default.explosions.constant-radius", nachoJson.constantExplosions);
		set("settings.event.fire-entity-explode-event", nachoJson.fireEntityExplodeEvent);
		set("world-settings.default.explosions.reduced-density-rays", nachoJson.reducedDensityRays);
		set("settings.brand-name", nachoJson.serverBrandName);
		set("settings.stop-decoding-itemstack-on-place", nachoJson.stopDecodingItemStackOnPlace);
		set("settings.anti-crash", nachoJson.enableAntiCrash);
		set("world-settings.default.infinite-water-sources", nachoJson.infiniteWaterSources);
		set("settings.event.fire-leaf-decay-event", nachoJson.leavesDecayEvent);
		set("world-settings.default.entity.mob-ai", nachoJson.enableMobAI);
		set("world-settings.default.entity.mob-sound", nachoJson.enableMobSound);
		set("world-settings.default.entity.entity-activation", nachoJson.enableEntityActivation);
		set("world-settings.default.entity.endermite-spawning", nachoJson.endermiteSpawning);
		set("world-settings.default.enable-lava-to-cobblestone", nachoJson.enableLavaToCobblestone);
		set("settings.event.fire-player-move-event", nachoJson.firePlayerMoveEvent);
		set("world-settings.default.physics.disable-place", nachoJson.disablePhysicsPlace);
		set("world-settings.default.physics.disable-update", nachoJson.disablePhysicsUpdate);
		set("world-settings.default.block-operations", nachoJson.doBlocksOperations);
		set("world-settings.default.unload-chunks", nachoJson.doChunkUnload);
		set("settings.chunk.threads", nachoJson.chunkThreads);
		set("settings.chunk.players-per-thread", nachoJson.playersPerThread);
		set("settings.use-tcp-nodelay", nachoJson.enableTCPNODELAY);
		set("settings.fixed-pools.use-fixed-pools-for-explosions", nachoJson.useFixedPoolForTNT);
		set("settings.fixed-pools.size", nachoJson.fixedPoolSize);
		set("settings.faster-cannon-tracker", nachoJson.useFasterCannonTracker);
		set("world-settings.default.disable-sponge-absorption", nachoJson.disableSpongeAbsorption);
		set("settings.fix-eat-while-running", nachoJson.fixEatWhileRunning);
		set("settings.hide-projectiles-from-hidden-players", nachoJson.hideProjectilesFromHiddenPlayers);

		old_config.delete();
	}

	static void readConfig(Class<?> clazz, Object instance) {
		for (Method method : clazz.getDeclaredMethods()) {
			if (Modifier.isPrivate(method.getModifiers())) {
				if (method.getParameterTypes().length == 0 && method.getReturnType() == Void.TYPE) {
					try {
						method.setAccessible(true);
						method.invoke(instance);
					} catch (InvocationTargetException ex) {
						throw Throwables.propagate(ex.getCause());
					} catch (Exception ex) {
						LOGGER.log(Level.ERROR, "Error invoking " + method, ex);
					}
				}
			}
		}

		try {
			config.save(CONFIG_FILE);
			// c.saveComments(CONFIG_FILE);
		} catch (IOException ex) {
			LOGGER.log(Level.ERROR, "Could not save " + CONFIG_FILE, ex);
		}
	}

	private static void set(String path, Object val) {
		config.set(path, val);
	}

	private static boolean getBoolean(String path, boolean def) {
		config.addDefault(path, def);
		return config.getBoolean(path, config.getBoolean(path));
	}

	private static double getDouble(String path, double def) {
		config.addDefault(path, def);
		return config.getDouble(path, config.getDouble(path));
	}

	private static float getFloat(String path, float def) {
		config.addDefault(path, def);
		return config.getFloat(path, config.getFloat(path));
	}

	private static int getInt(String path, int def) {
		config.addDefault(path, def);
		return config.getInt(path, config.getInt(path));
	}

	private static <T> List getList(String path, T def) {
		config.addDefault(path, def);
		return config.getList(path, config.getList(path));
	}

	private static String getString(String path, String def) {
		config.addDefault(path, def);
		return config.getString(path, config.getString(path));
	}

}

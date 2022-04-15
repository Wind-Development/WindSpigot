package ga.windpvp.windspigot.config;

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

import ga.windpvp.windspigot.config.TimingsCheck;

public class WindSpigotConfig {

	private static final Logger LOGGER = LogManager.getLogger(WindSpigotConfig.class);
	private static File CONFIG_FILE;
	protected static final YamlCommenter c = new YamlCommenter();
	private static final String HEADER = "This is the main configuration file for WindSpigot.\n"
			+ "As you can see, there's tons to configure. Some options may impact gameplay, so use\n"
			+ "with caution, and make sure you know what each option does before configuring.\n" + "\n"
			+ "If you need help with the configuration or have any questions related to WindSpigot,\n"
			+ "join us in our Discord.\n" + "\n" + "Discord: https://discord.gg/kAbTsFkbmN\n";

	static YamlConfiguration config;
	static int version;

	public static void init(File configFile) {
		CONFIG_FILE = configFile;
		config = new YamlConfiguration();
		try {
			System.out.println("Loading WindSpigot config from " + configFile.getName());
			config.load(CONFIG_FILE);
		} catch (IOException ignored) {
		} catch (InvalidConfigurationException ex) {
			LOGGER.log(Level.ERROR, "Could not load windspigot.yml, please correct your syntax errors", ex);
			throw Throwables.propagate(ex);
		}
		config.options().copyDefaults(true);

		int configVersion = 9; // Update this every new configuration update

    version = getInt("config-version", configVersion);
		set("config-version", configVersion);
		c.setHeader(HEADER);
		c.addComment("config-version", "Configuration version, do NOT modify this!");
		readConfig(WindSpigotConfig.class, null);
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
			c.saveComments(CONFIG_FILE);
		} catch (Exception ex) {
			LOGGER.log(Level.ERROR, "Could not save " + CONFIG_FILE, ex);
			LOGGER.warn("Please regenerate your windspigot.yml file to prevent this issue! The server will run with the default config for now.");
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

	public static boolean disableTracking;
	public static int trackingThreads;

	private static void tracking() {
		disableTracking = !getBoolean("settings.async.entity-tracking.enable", true);
		c.addComment("settings.async.entity-tracking.enable", "Enables asynchronous entity tracking");
		trackingThreads = getInt("settings.async.entity-tracking.threads", 3);
		c.addComment("settings.async.entity-tracking.threads",
				"The amount of threads to use when asynchronous entity tracking is enabled.");
		
		c.addComment("settings.async.entity-tracking", "Configuration for the async entity tracker.");
	}

	public static boolean threadAffinity;

	private static void threadAffinity() {
		threadAffinity = getBoolean("settings.thread-affinity", false);
		c.addComment("settings.thread-affinity",
				"Only switch to true if your OS is properly configured!! (See https://github.com/OpenHFT/Java-Thread-Affinity#isolcpus) When properly configured on the OS this allocates an entire cpu core to the server, it improves performance but uses more cpu.");
	}

	public static boolean mobAiCmd;

	private static void mobAiCmd() {
		mobAiCmd = getBoolean("settings.command.mob-ai", true);
		c.addComment("settings.command.mob-ai",
				"Enables the command \"/mobai\" which toggles mob ai. Users require the permission windspigot.command.mobai");
	}

	public static boolean parallelWorld;

	private static void parallelWorld() {
		parallelWorld = getBoolean("settings.async.parallel-world", true);
		// Disable timings by making timings check a variable (Code from api can't
		// access server code, so we have to do this)
		// Please open a PR if you know of a better method to do this.
		if (parallelWorld) {
			TimingsCheck.setEnableTimings(false);
		} else {
			TimingsCheck.setEnableTimings(true);
		}
		c.addComment("settings.async.parallel-world",
				"Enables async world ticking, ticking is faster if there are more worlds. Timings and other profilers are not supported when using this. Please take frequent backups whilst using this.");
	}

	public static boolean limitedMobSpawns;

	private static void limitedMobSpawns() {
		limitedMobSpawns = getBoolean("settings.limited-mob-spawns", false);
		c.addComment("settings.limited-mob-spawns",
				"Disables mob spawning if TPS is lower than the specified threshold.");
	}

	public static int limitedMobSpawnsThreshold;

	private static void limitedMobSpawnsThreshold() {
		limitedMobSpawnsThreshold = getInt("settings.limited-mob-spawns-threshold", 18);
		c.addComment("settings.limited-mob-spawns-threshold",
				"Threshold to disable mob spawning. Does not apply if limited mob spawns is not enabled.");
	}
	
	// FlamePaper start - 0117-Pearl-through-blocks
	public static boolean pearlPassthroughFenceGate;

	private static void pearlPassthroughFenceGate() {
		pearlPassthroughFenceGate = getBoolean("settings.pearl-passthrough.fence_gate", true);
		c.addComment("settings.pearl-passthrough.fence_gate", "Allows pearls to pass through fences.");
	}

	public static boolean pearlPassthroughTripwire;

	private static void pearlPassthroughTripwire() {
		pearlPassthroughTripwire = getBoolean("settings.pearl-passthrough.tripwire", true);
		c.addComment("settings.pearl-passthrough.tripwire", "Allows pearls to pass through tripwires.");
	}

	public static boolean pearlPassthroughSlab;

	private static void pearlPassthroughSlab() {
		pearlPassthroughSlab = getBoolean("settings.pearl-passthrough.slab", true);
		c.addComment("settings.pearl-passthrough.slab", "Allows pearls to pass through slabs.");
	}

	public static boolean pearlPassthroughCobweb;

	private static void pearlPassthroughCobweb() {
		pearlPassthroughCobweb = getBoolean("settings.pearl-passthrough.cobweb", true);
		c.addComment("settings.pearl-passthrough.cobweb", "Allows pearls to pass through cobwebs.");
	}

	public static boolean pearlPassthroughBed;

	private static void pearlPassthroughBed() {
		pearlPassthroughBed = getBoolean("settings.pearl-passthrough.bed", false);
		c.addComment("settings.pearl-passthrough.bed", "Allows pearls to pass through beds.");
	}
	// FlamePaper end
	
	private static void async() {
		c.addComment("settings.async", "Configuration for asynchronous things.");
	}
	
	private static void pearlPassthrough() {
		c.addComment("settings.pearl-passthrough", "Configuration for ender pearls passing through certain blocks. (Credits to FlamePaper)");
	}
	
	// From
	// https://github.com/Argarian-Network/NachoSpigot/tree/async-kb-hit
	public static int nettyThreads;
    public static int combatThreadTPS;

    private static void nettyThreads() {
        nettyThreads = getInt("settings.async.netty-threads", 4);
        c.addComment("settings.async.netty-threads", "Number of netty threads.");
        combatThreadTPS = getInt("settings.async.combat-thread-tps", 40);
        c.addComment("settings.async.combat-thread-tps", "Combat thread TPS for async knockback and hit detection.");
    }

    public static boolean asyncHitDetection;
    public static boolean asyncKnockback;

    private static void asyncPackets() {
        asyncHitDetection = getBoolean("settings.async.hit-detection", true);
        c.addComment("settings.async.hit-detection", "Enables asynchronous hit detection. This increases overall cpu usage, but sends hit detection packets faster. Disable this if you do not run a pvp server.");
        asyncKnockback = getBoolean("settings.async.knockback", true);
        c.addComment("settings.async.knockback", "Enables asynchronous knockback. This increases overall cpu usage, but sends knockback packets faster. Disable this if you do not run a pvp server.");
    }
    
	public static boolean pingCmd;
	public static String pingSelfCmdString;
	public static String pingOtherCmdString;

	private static void pingCmd() {
		pingCmd = getBoolean("settings.command.ping.enable", true);
		
		pingSelfCmdString = getString("settings.command.ping.self-ping-msg", "&bYour ping: &3%ping%");
		pingOtherCmdString = getString("settings.command.ping.other-ping-msg", "&3%player%'s &bping: &3%ping%");
		
		c.addComment("settings.command.ping.enable",
				"Enables the command \"/ping <player>\" which shows player ping. Users require the permission windspigot.command.ping");
		
		c.addComment("settings.command.ping.self-ping-msg", "The message displayed for the /ping command");
		c.addComment("settings.command.ping.other-ping-msg", "The message displayed for the /ping <player> command");
	}
	
	public static boolean asyncTnt;

	private static void asyncTnt() {
		asyncTnt = getBoolean("settings.async.tnt", true);
		c.addComment("settings.async.tnt", "Enables NachoSpigot's async tnt.");
	}
	
	public static boolean statistics;
	
	private static void statistics() {
		statistics = getBoolean("settings.statistics", true);
		c.addComment("settings.statistics",
				"Enables WindSpigot statistics. This allows developers to see how many WindSpigot servers are running. This has no performance impact and is completely anonymous, but you can opt out of this if you want.");
	}
	
}

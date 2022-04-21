package ga.windpvp.windspigot.config;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.sugarcanemc.sugarcane.util.yaml.YamlCommenter;

import com.google.common.base.Throwables;

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

		int configVersion = 14; // Update this every new configuration update

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
			LOGGER.warn("Waiting for 5 seconds so this can be read...");
			
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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
		disableTracking = !getBoolean("async.entity-tracking.enable", true);
		trackingThreads = getInt("async.entity-tracking.threads", 4);
		c.addComment("async.entity-tracking.enable", "Enables asynchronous entity tracking");
		c.addComment("async.entity-tracking.threads",
				"The amount of threads to use when asynchronous entity tracking is enabled.");
		c.addComment("async.entity-tracking", "Configuration for the async entity tracker.");
	}

	public static boolean threadAffinity;

	private static void threadAffinity() {
		threadAffinity = getBoolean("thread-affinity", false);
		c.addComment("thread-affinity",
				"Only switch to true if your OS is properly configured!! (See https://github.com/OpenHFT/Java-Thread-Affinity#isolcpus) When properly configured on the OS this allocates an entire cpu core to the server, it improves performance but uses more cpu.");
	}

	public static boolean mobAiCmd;

	private static void mobAiCmd() {
		mobAiCmd = getBoolean("command.mob-ai", true);
		c.addComment("command.mob-ai",
				"Enables the command \"/mobai\" which toggles mob ai. Users require the permission windspigot.command.mobai");
	}

	public static boolean parallelWorld;

	private static void parallelWorld() {
		parallelWorld = getBoolean("async.parallel-world", true);
		// Disable timings by making timings check a variable (Code from api can't
		// access server code, so we have to do this)
		// Please open a PR if you know of a better method to do this.
		if (parallelWorld) {
			TimingsCheck.setEnableTimings(false);
		} else {
			TimingsCheck.setEnableTimings(true);
		}
		c.addComment("async.parallel-world",
				"Enables async world ticking, ticking is faster if there are more worlds. Timings and other profilers are not supported when using this.");
	}

	public static boolean limitedMobSpawns;

	private static void limitedMobSpawns() {
		limitedMobSpawns = getBoolean("limited-mob-spawns", false);
		c.addComment("limited-mob-spawns",
				"Disables mob spawning if TPS is lower than the specified threshold.");
	}

	public static int limitedMobSpawnsThreshold;

	private static void limitedMobSpawnsThreshold() {
		limitedMobSpawnsThreshold = getInt("limited-mob-spawns-threshold", 18);
		c.addComment("limited-mob-spawns-threshold",
				"Threshold to disable mob spawning. Does not apply if limited mob spawns is not enabled.");
	}
	
	// FlamePaper start - 0117-Pearl-through-blocks
	public static boolean pearlPassthroughFenceGate;

	private static void pearlPassthroughFenceGate() {
		pearlPassthroughFenceGate = getBoolean("pearl-passthrough.fence-gate", true);
		c.addComment("pearl-passthrough.fence-gate", "Allows pearls to pass through fences.");
	}

	public static boolean pearlPassthroughTripwire;

	private static void pearlPassthroughTripwire() {
		pearlPassthroughTripwire = getBoolean("pearl-passthrough.tripwire", true);
		c.addComment("pearl-passthrough.tripwire", "Allows pearls to pass through tripwires.");
	}

	public static boolean pearlPassthroughSlab;

	private static void pearlPassthroughSlab() {
		pearlPassthroughSlab = getBoolean("pearl-passthrough.slab", true);
		c.addComment("pearl-passthrough.slab", "Allows pearls to pass through slabs.");
	}

	public static boolean pearlPassthroughCobweb;

	private static void pearlPassthroughCobweb() {
		pearlPassthroughCobweb = getBoolean("pearl-passthrough.cobweb", true);
		c.addComment("pearl-passthrough.cobweb", "Allows pearls to pass through cobwebs.");
	}

	public static boolean pearlPassthroughBed;

	private static void pearlPassthroughBed() {
		pearlPassthroughBed = getBoolean("pearl-passthrough.bed", false);
		c.addComment("pearl-passthrough.bed", "Allows pearls to pass through beds.");
	}
	// FlamePaper end
	
	private static void async() {
		c.addComment("async", "Configuration for asynchronous things.");
	}
	
	private static void pearlPassthrough() {
		c.addComment("pearl-passthrough", "Configuration for ender pearls passing through certain blocks. (Credits to FlamePaper)");
	}
	
	// From
	// https://github.com/Argarian-Network/NachoSpigot/tree/async-kb-hit
    public static int combatThreadTPS;

    private static void combatThread() {
        combatThreadTPS = getInt("async.combat-thread-tps", 40);        
        c.addComment("async.combat-thread-tps", "Combat thread TPS for async knockback and hit detection.");
    }

    public static boolean asyncHitDetection;
    public static boolean asyncKnockback;

    private static void asyncPackets() {
        asyncHitDetection = getBoolean("async.hit-detection", true);
        asyncKnockback = getBoolean("async.knockback", true);
        c.addComment("async.hit-detection", "Enables asynchronous hit detection. This increases overall cpu usage, but sends hit detection packets faster. Disable this if you do not run a pvp server.");
        c.addComment("async.knockback", "Enables asynchronous knockback. This increases overall cpu usage, but sends knockback packets faster. Disable this if you do not run a pvp server.");
    }
    
	public static boolean pingCmd;
	public static String pingSelfCmdString;
	public static String pingOtherCmdString;

	private static void pingCmd() {
		pingCmd = getBoolean("command.ping.enable", true);
		
		pingSelfCmdString = getString("command.ping.self-ping-msg", "&bYour ping: &3%ping%");
		pingOtherCmdString = getString("command.ping.other-ping-msg", "&3%player%'s &bping: &3%ping%");
		c.addComment("command.ping.enable",
				"Enables the command \"/ping <player>\" which shows player ping. Users require the permission windspigot.command.ping");
		c.addComment("command.ping.self-ping-msg", "The message displayed for the /ping command");
		c.addComment("command.ping.other-ping-msg", "The message displayed for the /ping <player> command");
	}
	
	public static boolean asyncTnt;

	private static void asyncTnt() {
		asyncTnt = getBoolean("async.tnt", true);
		c.addComment("async.tnt", "Enables async tnt (Credits to NachoSpigot).");
	}
	
	public static boolean statistics;
	
	private static void statistics() {
		statistics = getBoolean("statistics", true);
		c.addComment("statistics",
				"Enables WindSpigot statistics. This allows developers to see how many WindSpigot servers are running. This has no performance impact and is completely anonymous, but you can opt out of this if you want.");
	}
	
	public static int hitDelay;
	
	private static void hitDelay() {
		hitDelay = getInt("hit-delay", 20);
		c.addComment("hit-delay", "This sets the delay between player attacks, 20 is the default. Setting this to 0 allows for no hit delay.");
	}
	
	public static double potionSpeed;
	
	private static void potionSpeed() {
		potionSpeed = getDouble("potion-speed-offset", 0);
		c.addComment("potion-speed-offset", "This sets the speed of pots, 0 is the default speed. Setting this higher makes potions splash faster. This config option accepts decimals.");
	}
	
	public static boolean showPlayerIps;
	
	private static void showPlayerIps() {
		showPlayerIps = getBoolean("show-player-ips", true);
		c.addComment("show-player-ips", "Disabling this will prevent logging of player ips in console.");
	}
	
	public static boolean chunkThread;
	public static int chunkThreadTps;
	
	public static int maxChunkSends;
	public static boolean adaptive;
	public static int chunkPingThreshold;
	
	private static void chunkThread() {
		chunkThread = getBoolean("async.chunk-packet-batching.enabled", false);
		chunkThreadTps = getInt("async.chunk-packet-batching.tps", 2);
		maxChunkSends = getInt("async.chunk-packet-batching.max-chunk-sends", 5);
		adaptive = getBoolean("async.chunk-packet-batching.adaptive", true);
		chunkPingThreshold = getInt("async.chunk-packet-batching.ping-threshold", 120);
		
		c.addComment("async.chunk-packet-batching.enabled", "This enables a chunk packet thread. This does not improve server performance, this is made to help players with high ping.");
		c.addComment("async.chunk-packet-batching.tps", "This is the TPS of the chunk thread. A lower setting helps players with high ping, but sends chunks slower.");
		c.addComment("async.chunk-packet-batching.max-chunk-sends", "This is the maximum number of chunk packets that can be sent per chunk thread tick per player.");
		c.addComment("async.chunk-packet-batching.adaptive", "This enables adaptive chunk packet sending, only laggy/high ping players will receive chunks slower.");
		c.addComment("async.chunk-packet-batching.ping-threshold", "This is the threshold for players to be considered high ping. Only high ping players will receive chunk packets slower if adaptive mode is enabled.");
		
		c.addComment("async.chunk-packet-batching", "Configuration for the chunk thread");
	}
}

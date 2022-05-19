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
import org.bukkit.entity.EntityType;
import org.sugarcanemc.sugarcane.util.yaml.YamlCommenter;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

import ga.windpvp.windspigot.WindSpigot;
import ga.windpvp.windspigot.async.pathsearch.AsyncNavigation;
import ga.windpvp.windspigot.entity.EntityTickLimiter;
import me.elier.nachospigot.config.NachoConfig;

@SuppressWarnings("unused")
public class WindSpigotConfig {

	private static final Logger LOGGER = LogManager.getLogger(WindSpigotConfig.class);
	private static File CONFIG_FILE;
	protected static final YamlCommenter c = new YamlCommenter();
	private static final String HEADER = "This is the main configuration file for WindSpigot.\n"
			+ "As you can see, there's tons to configure. Some options may impact gameplay, so use\n"
			+ "with caution, and make sure you know what each option does before configuring.\n" + "\n"
			+ "If you need help with the configuration or have any questions related to WindSpigot,\n"
			+ "join us in our Discord.\n" + "\n" + "Discord: https://discord.gg/kAbTsFkbmN\n";

	public static YamlConfiguration config;
	static int version;

	public static void init(File configFile) {
		CONFIG_FILE = configFile;
		config = new YamlConfiguration();
		try {
			WindSpigot.LOGGER.info("Loading WindSpigot config from " + configFile.getName());
			config.load(CONFIG_FILE);
		} catch (IOException ignored) {
		} catch (InvalidConfigurationException ex) {
			LOGGER.log(Level.ERROR, "Could not load windspigot.yml, please correct your syntax errors", ex);
			throw Throwables.propagate(ex);
		}
		config.options().copyDefaults(true);

		int configVersion = 25; // Update this every new configuration update

		version = getInt("config-version", configVersion);
		set("config-version", configVersion);
		c.setHeader(HEADER);
		c.addComment("config-version", "Configuration version, do NOT modify this!");
		readConfig(WindSpigotConfig.class, null);
	}
	
	// Not private as the config is read by calling all private methods with 0 params
	static void makeReadable() {
		LOGGER.warn("Waiting for 10 seconds so this can be read...");
		
		try {
			TimeUnit.SECONDS.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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

			makeReadable();
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
	
	// General header comments
	private static void headerComments() {
		c.addComment("settings.async", "Configuration for asynchronous things.");
		c.addComment("settings.pearl-passthrough", "Configuration for ender pearls passing through certain blocks. (Credits to FlamePaper)");
		c.addComment("settings.command", "Configuration for WindSpigot's commands");
		c.addComment("settings.max-tick-time", "Configuration for maximum entity tick time");
	}
	
	public static boolean disableTracking;
	public static int trackingThreads;

	private static void tracking() {
		disableTracking = !getBoolean("settings.async.entity-tracking.enable", true);
		c.addComment("settings.async.entity-tracking.enable", "Enables asynchronous entity tracking");
		trackingThreads = getInt("settings.async.entity-tracking.threads", 5);
		c.addComment("settings.async.entity-tracking.threads",
				"The amount of threads to use when asynchronous entity tracking is enabled.");
		
		c.addComment("settings.async.entity-tracking", "Configuration for the async entity tracker.");
	}

	public static boolean threadAffinity;

	private static void threadAffinity() {
		threadAffinity = getBoolean("settings.thread-affinity", false);
		c.addComment("settings.thread-affinity",
				"Only switch to true if your OS is properly configured!! (See https://github.com/OpenHFT/Java-Thread-Affinity#isolcpus) \nWhen properly configured on the OS this allocates an entire cpu core to the server, it improves performance but uses more cpu.");
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
				"Enables async world ticking, ticking is faster if there are more worlds. Timings and other profilers are not supported when using this.");
	}

	public static boolean limitedMobSpawns;

	private static void limitedMobSpawns() {
		limitedMobSpawns = getBoolean("settings.limited-mob-spawns", false);
		c.addComment("settings.limited-mob-spawns",
				"Disables mob spawning if TPS is lower than the specified threshold.");
	}

	public static double limitedMobSpawnsThreshold;

	private static void limitedMobSpawnsThreshold() {
		limitedMobSpawnsThreshold = getDouble("settings.limited-mob-spawns-threshold", 18);
		c.addComment("settings.limited-mob-spawns-threshold",
				"Threshold to disable mob spawning. This does not apply if limited mob spawns is not enabled. This option accepts decimals.");
	}
	
	// FlamePaper start - 0117-Pearl-through-blocks
	public static boolean pearlPassthroughFenceGate;

	private static void pearlPassthroughFenceGate() {
		pearlPassthroughFenceGate = getBoolean("settings.pearl-passthrough.fence-gate", false);
		c.addComment("settings.pearl-passthrough.fence-gate", "Allows pearls to pass through fences.");
	}

	public static boolean pearlPassthroughTripwire;

	private static void pearlPassthroughTripwire() {
		pearlPassthroughTripwire = getBoolean("settings.pearl-passthrough.tripwire", false);
		c.addComment("settings.pearl-passthrough.tripwire", "Allows pearls to pass through tripwires.");
	}

	public static boolean pearlPassthroughSlab;

	private static void pearlPassthroughSlab() {
		pearlPassthroughSlab = getBoolean("settings.pearl-passthrough.slab", false);
		c.addComment("settings.pearl-passthrough.slab", "Allows pearls to pass through slabs.");
	}

	public static boolean pearlPassthroughCobweb;

	private static void pearlPassthroughCobweb() {
		pearlPassthroughCobweb = getBoolean("settings.pearl-passthrough.cobweb", false);
		c.addComment("settings.pearl-passthrough.cobweb", "Allows pearls to pass through cobwebs.");
	}

	public static boolean pearlPassthroughBed;

	private static void pearlPassthroughBed() {
		pearlPassthroughBed = getBoolean("settings.pearl-passthrough.bed", false);
		c.addComment("settings.pearl-passthrough.bed", "Allows pearls to pass through beds.");
	}
	// FlamePaper end
	
	// From
	// https://github.com/Argarian-Network/NachoSpigot/tree/async-kb-hit
    public static int combatThreadTPS;

    private static void combatThread() {
        combatThreadTPS = getInt("settings.async.combat-thread-tps", 40);        
        c.addComment("settings.async.combat-thread-tps", "Combat thread TPS for async knockback.");
    }

    // public static boolean asyncHitDetection;
    public static boolean asyncKnockback;

    private static void asyncPackets() {
    	// We use Nacho's implementation of instant interactions for async hit detection
    	instantPlayInUseEntity = getBoolean("settings.async.hit-detection", true);
        asyncKnockback = getBoolean("settings.async.knockback", false);
        c.addComment("settings.async.hit-detection", "Enables instant hit detection. This overrides the \"instant-interaction\" setting (Credits to NachoSpigot).");
        c.addComment("settings.async.knockback", "Enables asynchronous knockback. This increases overall cpu usage, but sends knockback packets faster. Disable this if you do not run a pvp server. \nThis may be incompatible with a few plugins that listen to knockback packets. Test before using in production.");
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
		c.addComment("settings.async.tnt", "Enables async tnt (Credits to NachoSpigot).");
	}
	
	public static boolean statistics;
	
	private static void statistics() {
		statistics = getBoolean("settings.statistics", true);
		c.addComment("settings.statistics",
				"Enables WindSpigot statistics. This allows developers to see how many WindSpigot servers are running. \nThis has no performance impact and is completely anonymous, but you can opt out of this if you want.");
	}
	
	public static int hitDelay;
	
	private static void hitDelay() {
		hitDelay = getInt("settings.hit-delay", 20);
		c.addComment("settings.hit-delay", "This sets the delay between player attacks, 20 is the default. Setting this to 0 allows for no hit delay.");
	}
	
	public static double potionSpeed;
	
	private static void potionSpeed() {
		potionSpeed = getDouble("settings.potion-speed-offset", 0);
		c.addComment("settings.potion-speed-offset", "This sets the speed offset of splash potions, 0 is the default speed. Setting this higher makes potions splash faster. \nThis config option accepts decimals.");
	}
	
	public static boolean showPlayerIps;
	
	private static void showPlayerIps() {
		showPlayerIps = getBoolean("settings.show-player-ips", true);
		c.addComment("settings.show-player-ips", "Disabling this will prevent display of player ips in the console.");
	}
	
	public static boolean modernKeepalive;
	
	private static void modernKeepalive() {
		modernKeepalive = getBoolean("settings.modern-keep-alive", false);
		c.addComment("settings.modern-keep-alive", "This enables keep alive handling from modern Minecraft. This may break some plugins.");
	}
	
	public static boolean asyncPathSearches;
	public static int distanceToAsync;
	public static int pathSearchThreads;
	public static boolean ensurePathSearchAccuracy;
	
	@SuppressWarnings("unchecked")
	private static void asyncPathSearches() {
		asyncPathSearches = getBoolean("settings.async.path-searches.enabled", true);
		
		if (asyncPathSearches) {
			List<String> asyncSearchEntities = getList("settings.async.path-searches.entities",
					Lists.newArrayList("BAT", "BLAZE", "CHICKEN", "COW", "CREEPER", "ENDERMAN", "HORSE", "IRON_GOLEM",
							"MAGMA_CUBE", "MUSHROOM_COW", "PIG", "PIG_ZOMBIE", "RABBIT", "SHEEP", "SKELETON", "SILVERFISH",
							"SLIME", "SNOWMAN", "SQUID", "WITCH", "ZOMBIE"));
			
			List<EntityType> finalEntities = Lists.newArrayList();
			
			for (String entityName : asyncSearchEntities) {
				finalEntities.add(EntityType.fromName(entityName));
			}
			
			AsyncNavigation.addOffloadedEntities(finalEntities);
			
			distanceToAsync = getInt("settings.async.path-searches.distance-to-async", 0);
			AsyncNavigation.setMinimumDistanceForOffloading(distanceToAsync);
			
			pathSearchThreads = getInt("settings.async.path-searches.threads", 4);
			ensurePathSearchAccuracy = getBoolean("settings.async.path-searches.ensure-accuracy", true);
			
		} 
		c.addComment("settings.async.path-searches.enabled", "Enables async path searching for entities.");
		c.addComment("settings.async.path-searches.entities", "A list of entities that utilize async path searches. Removing entities from this list will ensure 100% vanilla behavior, but worse performance.");
		c.addComment("settings.async.path-searches.distance-to-async", "The mininum distance an entity is targeting to handle it async. Tune this based on how many entities your server will has.");
		c.addComment("settings.async.path-searches.threads", "The threads used for path searches. Tune this based on how many entities your server will has.");
		c.addComment("settings.async.path-searches.ensure-accuracy", "Ensures accuracy of async path searches, disabling this will result in possibly inaccurate targeting, but higher performance.");
		
		c.addComment("settings.async.path-searches", "Configuration for async entity path searches");
	}
	
	public static boolean debugMode;
	
	private static void debugMode() {
		debugMode = getBoolean("settings.debug-mode", false);
		c.addComment("settings.debug-mode", "This outputs information to developers in the console. There is no need to enable this.");
	}

	public static int tileMaxTickTime;
	public static int entityMaxTickTime;

	private static void maxTickTimes() {
		entityMaxTickTime = getInt("settings.max-tick-time.entity", 35);
		tileMaxTickTime = 1000; // We do not re-implement the tile entity tick cap, so we disable it by setting it to 1000
		
		c.addComment("settings.max-tick-time.entity", "The maximum time that entities can take to tick before moving on. This may break some gameplay, so set to 1000 to disable. \nFor reference, there are 50 ms in a tick.");
	}
	
	public static boolean stopMobSpawnsDuringOverload;
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	private static void skippableEntities() {
		List<String> skippableEntities = getList("settings.max-tick-time.skippable-entities",
				Lists.newArrayList("BAT", "BLAZE", "CHICKEN", "COW", "CREEPER", "ENDERMAN", "HORSE", "IRON_GOLEM",
						"MAGMA_CUBE", "MUSHROOM_COW", "PIG", "PIG_ZOMBIE", "RABBIT", "SHEEP", "SKELETON", "SILVERFISH",
						"SLIME", "SNOWMAN", "SQUID", "WITCH", "ZOMBIE"));
		
		List<EntityType> finalEntities = Lists.newArrayList();
		
		for (String entityName : skippableEntities) {
			finalEntities.add(EntityType.fromName(entityName));
		}
		EntityTickLimiter.addSkippableEntities(finalEntities);
		
		stopMobSpawnsDuringOverload = getBoolean("settings.max-tick-time.limit-on-overload", false);
		
		c.addComment("settings.max-tick-time.skippable-entities", "The entity types that can be skipped when ticking. They will only be skipped if the server is lagging based on the set threshold. \nRemove entities from this list if their vanilla behavior is absolutely needed on your server.");
		c.addComment("settings.max-tick-time.limit-on-overload", "If the server should stop mob spawns when there are too many mobs to handle and some have to be skipped.");
	}
	
	public static boolean improvedHitDetection;

	private static void hitReg() {
		improvedHitDetection = getBoolean("settings.improved-hit-detection", true);
		c.addComment("settings.improved-hit-detection", "Enables the usage of an improved hit registration based on lag compensation and small other details. (Credits to NachoSpigot and the original plugin)");
	}
	
	public static boolean explosionAnimation;
	public static boolean explosionSounds;
	
	public static boolean spawnerAnimation;
	
	private static void particles() {
		explosionAnimation = getBoolean("settings.explosion-animation", true);
		explosionSounds = getBoolean("settings.explosion-sound", true);
		
		spawnerAnimation = getBoolean("settings.spawner-animation", true);
		
		c.addComment("settings.explosion-animation", "Enables explosion animations.");
		c.addComment("settings.explosion-sound", "Enables explosion sounds.");
		
		c.addComment("settings.spawner-animation", "Enables mob spawner particles.");
	}
	
	public static boolean weatherChange;
	
	private static void weatherChange() {
		weatherChange = getBoolean("settings.weather-change", true);
		
		c.addComment("settings.weather-change", "Enables changing of weather.");
	}
	
	public static boolean saveEmptyScoreboardTeams;

	private static void saveEmptyScoreboardTeams() {
		saveEmptyScoreboardTeams = getBoolean("settings.save-empty-scoreboard-teams", false);
		c.addComment("settings.save-empty-scoreboard-teams",
				"Toggles whether or not the server should save empty scoreboard teams");
	}

	public static boolean enableVersionCommand;
	public static boolean enablePluginsCommand;
	public static boolean enableReloadCommand;

	private static void commands() {
		enableVersionCommand = getBoolean("settings.commands.enable-version-command", true);
		c.addComment("settings.commands.enable-version-command", "Toggles the /version command");
		enablePluginsCommand = getBoolean("settings.commands.enable-plugins-command", true);
		c.addComment("settings.commands.enable-plugins-command", "Toggles the /plugins command");
		enableReloadCommand = getBoolean("settings.commands.enable-reload-command", false);
		c.addComment("settings.commands.enable-reload-command", "Toggles the /reload command");
	}

	public static boolean useFastOperators;

	private static void useFastOperators() {
		useFastOperators = getBoolean("settings.fast-operators", true);
		c.addComment("settings.fast-operators",
				"Enables Fast Operators, which uses a faster method for managing operators");
	}

	public static boolean patchProtocolLib;

	private static void patchProtocolLib() {
		patchProtocolLib = getBoolean("settings.patch-protocollib", true);
		c.addComment("settings.patch-protocollib",
				"Enables the ProtocolLib runtime patch (not required on ProtocolLib version 4.7+)");
	}

	public static boolean stopNotifyBungee;

	private static void stopNotifyBungee() {
		stopNotifyBungee = getBoolean("settings.stop-notify-bungee", false);
		c.addComment("settings.stop-notify-bungee", "Disables the firewall check when running BungeeCord");
	}

	public static boolean checkForMalware;

	private static void antiMalware() {
		checkForMalware = getBoolean("settings.anti-malware", true);
		c.addComment("settings.anti-malware", "Enables the built-in anti malware feature");
	}

	public static boolean kickOnIllegalBehavior;

	private static void kickOnIllegalBehavior() {
		kickOnIllegalBehavior = getBoolean("settings.kick-on-illegal-behavior", true);
		c.addComment("settings.kick-on-illegal-behavior",
				"Kicks players if they try to do an illegal action (e.g. using a creative mode action while not in creative mode.)");
	}

	public static boolean usePandaWire;

	private static void usePandaWire() {
		usePandaWire = getBoolean("settings.panda-wire", true);
		c.addComment("settings.panda-wire", "Optimizes redstone wires.");
	}

	public static boolean fireEntityExplodeEvent;
	public static boolean firePlayerMoveEvent;
	public static boolean leavesDecayEvent;

	private static void fireEntityExplodeEvent() {
		fireEntityExplodeEvent = getBoolean("settings.event.fire-entity-explode-event", true);
		c.addComment("settings.event.fire-entity-explode-event", "Toggles the entity explode event");
		firePlayerMoveEvent = getBoolean("settings.event.fire-player-move-event", true);
		c.addComment("settings.event.fire-player-move-event", "Toggles the player move event");
		leavesDecayEvent = getBoolean("settings.event.fire-leaf-decay-event", true);
		c.addComment("settings.event.fire-leaf-decay-event", "Toggles the leaf decay event");
	}

	public static String serverBrandName;

	private static void serverBrandName() {
		serverBrandName = getString("settings.brand-name", "WindSpigot");
		c.addComment("settings.brand-name",
				"Changes the brand name of the server.\nThis will show in statistics, server lists, client crashes,\n and in the client debug screen. (accessed by pressing F3)");
	}

	public static boolean stopDecodingItemStackOnPlace;

	private static void stopDecodingItemStackOnPlace() {
		stopDecodingItemStackOnPlace = getBoolean("settings.stop-decoding-itemstack-on-place", true);
		c.addComment("settings.stop-decoding-itemstack-on-place", "Disables decoding itemstacks when not needed");
	}

	public static boolean enableAntiCrash;

	private static void enableAntiCrash() {
		enableAntiCrash = getBoolean("settings.anti-crash", true);
		c.addComment("settings.anti-crash",
				"Kicks players if they try to do an action that would/might crash the server");
	}

	public static int chunkThreads; // PaperSpigot - Bumped value
	public static int playersPerThread;

	private static void chunk() {
		chunkThreads = getInt("settings.chunk.threads", 2);
		c.addComment("settings.chunk.threads", "The amount of threads used for chunks");
		playersPerThread = getInt("settings.chunk.players-per-thread", 50);
		c.addComment("settings.chunk.players-per-thread", "The amount of players for each thread");
	}

	public static boolean enableTCPNODELAY;

	private static void enableTCPNODELAY() {
		enableTCPNODELAY = getBoolean("settings.use-tcp-nodelay", true);
		c.addComment("settings.use-tcp-nodelay", "Enables the TCP_NODELAY socket option");
	}

	public static int fixedPoolSize;

	private static void fixedPools() {
		fixedPoolSize = getInt("settings.fixed-pools.size", 3);
		c.addComment("settings.fixed-pools.size", "The size for the fixed thread pool for explosions.");
	}

	public static boolean useFasterCannonTracker;

	private static void useFasterCannonTracker() {
		useFasterCannonTracker = getBoolean("settings.faster-cannon-tracker", true);
		c.addComment("settings.faster-cannon-tracker", "Enables a faster cannon entity tracker");
	}

	public static boolean fixEatWhileRunning;

	private static void fixEatWhileRunning() {
		fixEatWhileRunning = getBoolean("settings.fix-eat-while-running", true);
		c.addComment("settings.fix-eat-while-running", "Fixes the eating while running bug");
	}

	public static boolean hideProjectilesFromHiddenPlayers;

	private static void hideProjectilesFromHiddenPlayers() {
		hideProjectilesFromHiddenPlayers = getBoolean("settings.hide-projectiles-from-hidden-players", false);
		c.addComment("settings.hide-projectiles-from-hidden-players", "Hides projectiles from hidden players");
	}

	public static boolean lagCompensatedPotions;

	private static void lagCompensatedPotions() {
		lagCompensatedPotions = getBoolean("settings.lag-compensated-potions", true);
		c.addComment("settings.lag-compensated-potions", "Enables lag compesation throwing potions");
	}

	public static boolean smoothPotting;

	private static void smoothPotting() {
		smoothPotting = getBoolean("settings.smooth-potting", true);
		c.addComment("settings.smooth-potting", "Make potion throwing smoother");
	}

	public static boolean antiEnderPearlGlitch;

	private static void antiEnderPearlGlitch() {
		antiEnderPearlGlitch = getBoolean("settings.anti-enderpearl-glitch", true);
		c.addComment("settings.anti-enderpearl-glitch", "Enables anti enderpearl glitch");
	}

	public static boolean disabledFallBlockAnimation;

	private static void disableFallAnimation() {
		disabledFallBlockAnimation = getBoolean("settings.disabled-block-fall-animation", false);
		c.addComment("settings.disabled-block-fall-animation", "Disables the fall animation for blocks");
	}

	public static boolean disableInfiniSleeperThreadUsage;

	private static void disableInfiniSleeperThreadUsage() {
		disableInfiniSleeperThreadUsage = getBoolean("settings.disable-infinisleeper-thread-usage", false);
		c.addComment("settings.disable-infinisleeper-thread-usage",
				"Disable infinisleeper thread usage, just enable this if you know what are you doing.");
	}

	public static boolean enableFastMath;

	private static void enableFastMath() {
		enableFastMath = getBoolean("settings.enable-fastmath", true);
		c.addComment("settings.enable-fastmath", "Enable Fast Math usage.");
	}

	public static int itemDirtyTicks;

	private static void itemDirtyTicks() {
		itemDirtyTicks = getInt("settings.item-dirty-ticks", 20);
		c.addComment("settings.item-dirty-ticks",
				"Controls the interval for the item-dirty check. Minecraft checks an item every tick to see if it was changed. This can be expensive because it also needs to check all NBT data. Spigot only checks for basic count/data/type data and does a deep check every 20 ticks by default.");
	}

	public static boolean enableTcpFastOpen;

	private static void enableTcpFastOpen() {
		enableTcpFastOpen = getBoolean("settings.use-tcp-fastopen", true);
		c.addComment("settings.use-tcp-fastopen", "Enables the TCP_FASTOPEN socket option");
	}

	public static int modeTcpFastOpen;

	private static void modeTcpFastOpen() {
		modeTcpFastOpen = getInt("settings.tcp-fastopen-mode", 1);
		c.addComment("settings.use-tcp-fastopen",
				"Options: 0 - Disabled.; 1 - TFO is enabled for outgoing connections (clients).; 2 - TFO is enabled for incoming connections (servers).; 3 - TFO is enabled for both clients and servers.");
	}

	public static boolean enableProtocolLibShim;

	private static void enableProtocolLibShim() {
		enableProtocolLibShim = getBoolean("settings.enable-protocollib-shim", true);
		c.addComment("settings.enable-protocollib-shim",
				"Enable ProtocolLib network shim. Allows ProtocolLib to work, but requires extra memory. Disable this if you don't use ProtocolLib!");
	}

	public static boolean instantPlayInUseEntity;

	private static void instantPlayInUseEntity() {
		instantPlayInUseEntity = getBoolean("settings.instant-interaction", false);
		c.addComment("settings.instant-interaction", "Disables delay of all interactions");
	}
}

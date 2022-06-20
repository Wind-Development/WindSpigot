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

@SuppressWarnings("unused")
public class WindSpigotConfig {

	private static final Logger LOGGER = LogManager.getLogger(WindSpigotConfig.class);
	public static File CONFIG_FILE;
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

		int configVersion = 28; // Update this every new configuration update

		version = getInt("config-version", configVersion);
		set("config-version", configVersion);
		c.setHeader(HEADER);
		readConfig(WindSpigotConfig.class, null);

		// Move nacho config options to our config
		/*if (!NachoConfig.hasMigrated) {
			// TODO: finish config migration/loading
			//NachoConfig.moveToWindSpigotConfig(NachoConfig.class, WindSpigotConfig.class);
			LOGGER.info("Successfully loaded nacho.yml into memory!");
			LOGGER.warn("Loading of nacho.yml will be removed in the future, so transfer the config options into windspigot.yml, then delete nacho.yml");
			makeReadable();
		}*/

		try {
			config.save(CONFIG_FILE);
			loadComments();
			c.saveComments(CONFIG_FILE);
		} catch (Exception ex) {
			LOGGER.log(Level.ERROR, "Could not save " + CONFIG_FILE, ex);
			
			LOGGER.warn("Please regenerate your windspigot.yml file to prevent this issue! The server will run with the default config for now.");

			makeReadable();
		}
		
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
	}
	
	static void loadComments() {
		c.addComment("config-version", "Configuration version, do NOT modify this!");

		c.addComment("settings.async", "Configuration for asynchronous things.");
		c.addComment("settings.pearl-passthrough", "Configuration for ender pearls passing through certain blocks. (Credits to FlamePaper)");
		c.addComment("settings.command", "Configuration for WindSpigot's commands");
		c.addComment("settings.max-tick-time", "Configuration for maximum entity tick time");
		c.addComment("settings.async.entity-tracking.enable", "Enables asynchronous entity tracking");
		c.addComment("settings.async.entity-tracking.threads", "The amount of threads used for async entity tracking, increase or decrease this based on your server load.");
		c.addComment("settings.async.entity-tracking", "Configuration for the async entity tracker.");
		c.addComment("settings.thread-affinity", "Only switch to true if your OS is properly configured!! (See https://github.com/OpenHFT/Java-Thread-Affinity#isolcpus) \nWhen properly configured on the OS this allocates an entire cpu core to the server, it improves performance but uses more cpu.");
		c.addComment("settings.command.mob-ai", "Enables the command \"/mobai\" which toggles mob ai. Users require the permission windspigot.command.mobai");
		c.addComment("settings.limited-mob-spawns", "Disables mob spawning if TPS is lower than the specified threshold.");	
		c.addComment("settings.limited-mob-spawns-threshold", "Threshold to disable mob spawning. This does not apply if limited mob spawns is not enabled. This option accepts decimals.");
		c.addComment("settings.pearl-passthrough.fence-gate", "Allows pearls to pass through fences.");
		c.addComment("settings.pearl-passthrough.tripwire", "Allows pearls to pass through tripwires.");
		c.addComment("settings.pearl-passthrough.slab", "Allows pearls to pass through slabs.");
		c.addComment("settings.pearl-passthrough.cobweb", "Allows pearls to pass through cobwebs.");
		c.addComment("settings.pearl-passthrough.bed", "Allows pearls to pass through beds.");
        c.addComment("settings.async.combat-thread-tps", "Combat thread TPS for async knockback.");
        c.addComment("settings.async.knockback", "Enables asynchronous knockback. This increases overall cpu usage, but sends knockback packets faster. Disable this if you do not run a pvp server. \nThis may be incompatible with a few plugins that listen to knockback packets. Test before using in production.");
		c.addComment("settings.command.ping.enable", "Enables the command \"/ping <player>\" which shows player ping. Users require the permission windspigot.command.ping");
		c.addComment("settings.command.ping.self-ping-msg", "The message displayed for the /ping command");
		c.addComment("settings.command.ping.other-ping-msg", "The message displayed for the /ping <player> command");
		c.addComment("settings.statistics", "Enables WindSpigot statistics. This allows developers to see how many WindSpigot servers are running. \nThis has no performance impact and is completely anonymous, but you can opt out of this if you want.");
		c.addComment("settings.hit-delay", "This sets the delay between player attacks, 20 is the default. Setting this to 0 allows for no hit delay.");
		c.addComment("settings.potion-speed-offset", "This sets the speed offset of splash potions, 0 is the default speed. Setting this higher makes potions splash faster. \nThis config option accepts decimals.");
		c.addComment("settings.show-player-ips", "Disabling this will prevent display of player ips in the console.");
		c.addComment("settings.modern-keep-alive", "This enables keep alive handling from modern Minecraft. This may break some plugins.");
		c.addComment("settings.async.path-searches.enabled", "Enables async path searching for entities.");
		c.addComment("settings.async.path-searches.entities", "A list of entities that utilize async path searches. Removing entities from this list will ensure 100% vanilla behavior, but worse performance.");
		c.addComment("settings.async.path-searches.distance-to-async", "The mininum distance an entity is targeting to handle it async. Tune this based on how many entities your server will has.");
		c.addComment("settings.async.path-searches.threads", "The threads used for path searches. Tune this based on how many entities your server will has.");
		c.addComment("settings.async.path-searches.ensure-accuracy", "Ensures accuracy of async path searches, disabling this will result in possibly inaccurate targeting, but higher performance.");
		c.addComment("settings.async.path-searches", "Configuration for async entity path searches");
		c.addComment("settings.debug-mode", "This outputs information to developers in the console. There is no need to enable this.");
		c.addComment("settings.max-tick-time.entity", "The maximum time that entities can take to tick before moving on. This may break some gameplay, so set to 1000 to disable. \nFor reference, there are 50 ms in a tick.");
		c.addComment("settings.max-tick-time.skippable-entities", "The entity types that can be skipped when ticking. They will only be skipped if the server is lagging based on the set threshold. \nRemove entities from this list if their vanilla behavior is absolutely needed on your server.");
		c.addComment("settings.max-tick-time.limit-on-overload", "If the server should stop mob spawns when there are too many mobs to handle and some have to be skipped.");
		c.addComment("settings.improved-hit-detection", "Enables the usage of an improved hit registration based on lag compensation and small other details. (Credits to NachoSpigot and the original plugin)");
		c.addComment("settings.animation.tnt", "Enables explosion animations.");
		c.addComment("settings.sound.tnt", "Enables explosion sounds.");
		c.addComment("settings.animation.spawner", "Enables mob spawner particles.");
		c.addComment("settings.weather-change", "Enables changing of weather.");
		c.addComment("settings.async.explosions.enable", "Enables async explosion calculatons.");
		c.addComment("settings.async.explosions.threads", "The threads used for async explosions");
		c.addComment("settings.explosions", "Configuration for async explosions");
		c.addComment("settings.max-reach.value", "The maximum reach a player can use, hits farther than this will be cancelled. Players can reach 6 blocks by default.");
		c.addComment("settings.max-reach.creative-bypass", "If creative players are immune to this reach cap");
		c.addComment("settings.spawners", "Configuration for MobSpawners");
		c.addComment("settings.spawners.initial-spawn-delay", "Initial spawn delay");
		c.addComment("settings.spawners.min-spawn-delay", "Minimum spawn delay");
		c.addComment("settings.spawners.max-spawn-delay", "Maximum spawn delay");
		c.addComment("settings.spawners.spawn-count", "Max amount of entities that can be spawned");
		c.addComment("settings.spawners.spawn-range", "Maximum area in which entities spawn");
		c.addComment("settings.spawners.max-nearby-entities", "Maximum number of nearby entities to avoid new spawns (Any value lower than 1 will disable this check)");
		c.addComment("settings.spawners.required-player-range", "Area in which the player is required to spawn entities");
		c.addComment("settings.fishing-rod-multiplier", "The speed multiplier of fishing rod projectiles.");
		
		// NachoSpigot stuff
		c.addComment("settings.save-empty-scoreboard-teams", "Toggles whether or not the server should save empty scoreboard teams");
		c.addComment("settings.command.version", "Enables the /version command");
		c.addComment("settings.command.plugins", "Enables the /plugins command");
		c.addComment("settings.command.reload", "Enables the /reload command (It is recommended to not use /reload)");
		c.addComment("settings.fast-operators", "Disables storage of operators, which deops all operators on server restarts");
		c.addComment("settings.patch-protocollib", "Enables the ProtocolLib runtime patch (not required on ProtocolLib version 4.7+)");
		c.addComment("settings.stop-notify-bungee", "Disables the firewall check when running BungeeCord");
		c.addComment("settings.anti-malware", "Enables the built-in anti malware feature");
		c.addComment("settings.kick-on-illegal-behavior", "Kicks players if they try to do an illegal action (e.g. using a creative mode action while not in creative mode.)");
		c.addComment("settings.panda-wire", "Optimizes redstone wires.");
		c.addComment("settings.event.fire-entity-explode-event", "Enables the entity explode event.");
		c.addComment("settings.event.fire-player-move-event", "Enables the player move event.");
		c.addComment("settings.event.fire-leaf-decay-event", "Enables the leaf decay event.");
		c.addComment("settings.brand-name", "Changes the brand name of the server.\nThis will show in statistics, server lists, client crashes,\n and in the client debug screen. (accessed by pressing F3)");
		c.addComment("settings.stop-decoding-itemstack-on-place", "Disables decoding itemstacks when not needed.");
		c.addComment("settings.anti-crash.enabled", "Kicks players if they try to do an action that could crash the server.");
		c.addComment("settings.chunk.threads", "The amount of threads used for chunks.");
		c.addComment("settings.chunk.players-per-thread", "The amount of players for each thread.");
		c.addComment("settings.use-tcp-nodelay", "Enables the TCP_NODELAY socket option.");
		c.addComment("settings.faster-cannon-tracker", "Enables a faster cannon entity tracker.");
		c.addComment("settings.fix-eat-while-running", "Fixes the eating while running bug.");
		c.addComment("settings.hide-projectiles-from-hidden-players", "Hides projectiles from hidden players.");
		c.addComment("settings.lag-compensated-potions", "Enables lag compesation for thrown potions.");
		c.addComment("settings.smooth-potting", "Makes potion throwing smoother.");
		c.addComment("settings.anti-enderpearl-glitch", "Blocks enderpearl glitching.");
		c.addComment("settings.disable-block-fall-animation", "Disables the fall animation for blocks.");
		c.addComment("settings.disable-infinisleeper-thread-usage", "Disable infinisleeper thread usage, only enable this if you know what are you doing.");
		c.addComment("settings.item-dirty-ticks", "Controls the interval for the item-dirty check. Minecraft checks an item every tick to see if it was changed. This can be expensive because it also needs to check all NBT data. Spigot only checks for basic count/data/type data and does a deep check every 20 ticks by default.");
		c.addComment("settings.tcp-fast-open.enabled", "Enables the TCP_FASTOPEN socket option.");
		c.addComment("settings.tcp-fast-open.mode", "Options: 0 - Disabled.; 1 - TFO is enabled for outgoing connections (clients).; 2 - TFO is enabled for incoming connections (servers).; 3 - TFO is enabled for both clients and servers.");
		c.addComment("settings.enable-protocollib-shim", "Enable ProtocolLib network shim. This allows ProtocolLib to work, but requires extra memory. Disable this if you don't use ProtocolLib!");
		c.addComment("settings.instant-interaction", "Disables delay of all interactions.");
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
		trackingThreads = getInt("settings.async.entity-tracking.threads", 5);
	}

	public static boolean threadAffinity;

	private static void threadAffinity() {
		threadAffinity = getBoolean("settings.thread-affinity", false);
	}

	public static boolean mobAiCmd;

	private static void mobAiCmd() {
		mobAiCmd = getBoolean("settings.command.mob-ai", true);
	}

	public static boolean limitedMobSpawns;

	private static void limitedMobSpawns() {
		limitedMobSpawns = getBoolean("settings.limited-mob-spawns", false);
	}

	public static double limitedMobSpawnsThreshold;

	private static void limitedMobSpawnsThreshold() {
		limitedMobSpawnsThreshold = getDouble("settings.limited-mob-spawns-threshold", 18);
	}
	
	// FlamePaper start - 0117-Pearl-through-blocks
	public static boolean pearlPassthroughFenceGate;

	private static void pearlPassthroughFenceGate() {
		pearlPassthroughFenceGate = getBoolean("settings.pearl-passthrough.fence-gate", false);
	}

	public static boolean pearlPassthroughTripwire;

	private static void pearlPassthroughTripwire() {
		pearlPassthroughTripwire = getBoolean("settings.pearl-passthrough.tripwire", false);
	}

	public static boolean pearlPassthroughSlab;

	private static void pearlPassthroughSlab() {
		pearlPassthroughSlab = getBoolean("settings.pearl-passthrough.slab", false);
	}

	public static boolean pearlPassthroughCobweb;

	private static void pearlPassthroughCobweb() {
		pearlPassthroughCobweb = getBoolean("settings.pearl-passthrough.cobweb", false);
	}

	public static boolean pearlPassthroughBed;

	private static void pearlPassthroughBed() {
		pearlPassthroughBed = getBoolean("settings.pearl-passthrough.bed", false);
	}
	// FlamePaper end
	
	// From
	// https://github.com/Argarian-Network/NachoSpigot/tree/async-kb-hit
    public static int combatThreadTPS;

    private static void combatThread() {
        combatThreadTPS = getInt("settings.async.combat-thread-tps", 40);        
    }

    // public static boolean asyncHitDetection;
    public static boolean asyncKnockback;

    private static void asyncPackets() {
        asyncKnockback = getBoolean("settings.async.knockback", false);
    }
    
	public static boolean pingCmd;
	public static String pingSelfCmdString;
	public static String pingOtherCmdString;

	private static void pingCmd() {
		pingCmd = getBoolean("settings.command.ping.enable", true);
		
		pingSelfCmdString = getString("settings.command.ping.self-ping-msg", "&bYour ping: &3%ping%");
		pingOtherCmdString = getString("settings.command.ping.other-ping-msg", "&3%player%'s &bping: &3%ping%");
	}
	
	public static boolean statistics;
	
	private static void statistics() {
		statistics = getBoolean("settings.statistics", true);
	}
	
	public static int hitDelay;
	
	private static void hitDelay() {
		hitDelay = getInt("settings.hit-delay", 20);
	}
	
	public static double potionSpeed;
	
	private static void potionSpeed() {
		potionSpeed = getDouble("settings.potion-speed-offset", 0);
	}
	
	public static boolean showPlayerIps;
	
	private static void showPlayerIps() {
		showPlayerIps = getBoolean("settings.show-player-ips", true);
	}
	
	public static boolean modernKeepalive;
	
	private static void modernKeepalive() {
		modernKeepalive = getBoolean("settings.modern-keep-alive", false);
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
	}
	
	public static boolean debugMode;
	
	private static void debugMode() {
		debugMode = getBoolean("settings.debug-mode", false);
	}

	public static int tileMaxTickTime;
	public static int entityMaxTickTime;

	private static void maxTickTimes() {
		entityMaxTickTime = getInt("settings.max-tick-time.entity", 35);
		tileMaxTickTime = 1000; // We do not re-implement the tile entity tick cap, so we disable it by setting it to 1000
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
	}
	
	public static boolean improvedHitDetection;

	private static void hitReg() {
		improvedHitDetection = getBoolean("settings.improved-hit-detection", true);
	}
	
	public static boolean explosionAnimation;
	public static boolean explosionSounds;

	public static boolean spawnerAnimation;
	
	private static void particlesAndSounds() {
		explosionAnimation = getBoolean("settings.animation.tnt", true);
		explosionSounds = getBoolean("settings.sound.tnt", true);
		spawnerAnimation = getBoolean("settings.animation.spawner", true);
	}

	public static int spawnersInitialSpawnDelay;
	public static int spawnersMinSpawnDelay;
	public static int spawnersMaxSpawnDelay;
	public static int spawnersSpawnCount;
	public static int spawnersSpawnRange;
	public static int spawnersMaxNearbyEntities;
	public static int spawnersRequiredPlayerRange;


	private static void spawners() {
		spawnersInitialSpawnDelay = getInt("settings.spawners.initial-spawn-delay", 20);
		spawnersMinSpawnDelay = getInt("settings.spawners.min-spawn-delay", 200);
		spawnersMaxSpawnDelay = getInt("settings.spawners.max-spawn-delay", 800);
		spawnersSpawnCount = getInt("settings.spawners.spawn-count", 4);
		spawnersSpawnRange = getInt("settings.spawners.spawn-range", 4);
		spawnersMaxNearbyEntities = getInt("settings.spawners.max-nearby-entities", 6);
		spawnersRequiredPlayerRange = getInt("settings.spawners.required-player-range", 16);
	}
	
	public static boolean weatherChange;
	
	private static void weatherChange() {
		weatherChange = getBoolean("settings.weather-change", true);
	}
	
	public static boolean asyncTnt;
	public static int fixedPoolSize;
	
	private static void asyncExplosions() {
		asyncTnt = getBoolean("settings.async.explosions.enable", true);
		fixedPoolSize = getInt("settings.async.explosions.threads", 3);
	}
	
	public static double maxReachSqrd;
	public static boolean creativeBypass;
	
	private static void maxReach() {
		maxReachSqrd = Math.pow(getDouble("settings.max-reach.value", 6), 2);
		creativeBypass = getBoolean("settings.max-reach.creative-bypass", true);
	}
	
	public static double fishingRodMultiplier;
	
	private static void fishingRodMultiplier() {
		fishingRodMultiplier = getDouble("settings.fishing-rod-multiplier", 1);
	}
	
	
	// Below are NachoSpigot config options
	public static boolean saveEmptyScoreboardTeams;

	private static void saveEmptyScoreboardTeams() {
		saveEmptyScoreboardTeams = getBoolean("settings.save-empty-scoreboard-teams", false);
	}

	public static boolean enableVersionCommand;
	public static boolean enablePluginsCommand;
	public static boolean enableReloadCommand;

	private static void commands() {
		enableVersionCommand = getBoolean("settings.command.version", true);
		enablePluginsCommand = getBoolean("settings.command.plugins", true);
		enableReloadCommand = getBoolean("settings.command.reload", false);
	}

	public static boolean useFastOperators;

	private static void useFastOperators() {
		useFastOperators = getBoolean("settings.fast-operators", false);
	}

	public static boolean patchProtocolLib;

	private static void patchProtocolLib() {
		patchProtocolLib = getBoolean("settings.patch-protocollib", true);
	}

	public static boolean stopNotifyBungee;

	private static void stopNotifyBungee() {
		stopNotifyBungee = getBoolean("settings.stop-notify-bungee", false);
	}

	public static boolean checkForMalware;

	private static void antiMalware() {
		checkForMalware = getBoolean("settings.anti-malware", true);
	}

	public static boolean kickOnIllegalBehavior;

	private static void kickOnIllegalBehavior() {
		kickOnIllegalBehavior = getBoolean("settings.kick-on-illegal-behavior", true);
	}

	public static boolean usePandaWire;

	private static void usePandaWire() {
		usePandaWire = getBoolean("settings.panda-wire", true);
	}

	public static boolean fireEntityExplodeEvent;
	public static boolean firePlayerMoveEvent;
	public static boolean leavesDecayEvent;

	private static void fireEntityExplodeEvent() {
		fireEntityExplodeEvent = getBoolean("settings.event.fire-entity-explode-event", true);
		firePlayerMoveEvent = getBoolean("settings.event.fire-player-move-event", true);
		leavesDecayEvent = getBoolean("settings.event.fire-leaf-decay-event", true);
	}

	public static String serverBrandName;

	private static void serverBrandName() {
		serverBrandName = getString("settings.brand-name", "WindSpigot");
	}

	public static boolean stopDecodingItemStackOnPlace;

	private static void stopDecodingItemStackOnPlace() {
		stopDecodingItemStackOnPlace = getBoolean("settings.stop-decoding-itemstack-on-place", true);
	}

	public static boolean enableAntiCrash;

	private static void enableAntiCrash() {
		enableAntiCrash = getBoolean("settings.anti-crash.enabled", true);
	}

	public static int chunkThreads; // PaperSpigot - Bumped value
	public static int playersPerThread;

	private static void chunk() {
		chunkThreads = getInt("settings.chunk.threads", 2);
		playersPerThread = getInt("settings.chunk.players-per-thread", 50);
	}

	public static boolean enableTCPNODELAY;

	private static void enableTCPNODELAY() {
		enableTCPNODELAY = getBoolean("settings.use-tcp-nodelay", true);
	}

	public static boolean useFasterCannonTracker;

	private static void useFasterCannonTracker() {
		useFasterCannonTracker = getBoolean("settings.faster-cannon-tracker", true);
	}

	public static boolean fixEatWhileRunning;

	private static void fixEatWhileRunning() {
		fixEatWhileRunning = getBoolean("settings.fix-eat-while-running", true);
	}

	public static boolean hideProjectilesFromHiddenPlayers;

	private static void hideProjectilesFromHiddenPlayers() {
		hideProjectilesFromHiddenPlayers = getBoolean("settings.hide-projectiles-from-hidden-players", false);
	}

	public static boolean lagCompensatedPotions;

	private static void lagCompensatedPotions() {
		lagCompensatedPotions = getBoolean("settings.lag-compensated-potions", true);
	}

	public static boolean smoothPotting;

	private static void smoothPotting() {
		smoothPotting = getBoolean("settings.smooth-potting", true);
	}

	public static boolean antiEnderPearlGlitch;

	private static void antiEnderPearlGlitch() {
		antiEnderPearlGlitch = getBoolean("settings.anti-enderpearl-glitch", true);
	}

	public static boolean disabledFallBlockAnimation;

	private static void disableFallAnimation() {
		disabledFallBlockAnimation = getBoolean("settings.disable-block-fall-animation", false);
	}

	public static boolean disableInfiniSleeperThreadUsage;

	private static void disableInfiniSleeperThreadUsage() {
		disableInfiniSleeperThreadUsage = getBoolean("settings.disable-infinisleeper-thread-usage", false);
	}

	public static int itemDirtyTicks;

	private static void itemDirtyTicks() {
		itemDirtyTicks = getInt("settings.item-dirty-ticks", 20);
	}

	public static boolean enableTcpFastOpen;

	private static void enableTcpFastOpen() {
		enableTcpFastOpen = getBoolean("settings.tcp-fast-open.enabled", true);
	}

	public static int modeTcpFastOpen;

	private static void modeTcpFastOpen() {
		modeTcpFastOpen = getInt("settings.tcp-fast-open.mode", 1);
	}

	public static boolean enableProtocolLibShim;

	private static void enableProtocolLibShim() {
		enableProtocolLibShim = getBoolean("settings.enable-protocollib-shim", true);
	}

	public static boolean instantPlayInUseEntity;

	private static void instantPlayInUseEntity() {
		instantPlayInUseEntity = getBoolean("settings.instant-interaction", false);
	}
}

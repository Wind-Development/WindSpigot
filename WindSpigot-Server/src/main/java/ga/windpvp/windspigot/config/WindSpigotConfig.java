package ga.windpvp.windspigot.config;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import ga.windpvp.windspigot.WindSpigot;
import ga.windpvp.windspigot.async.pathsearch.AsyncNavigation;
import ga.windpvp.windspigot.entity.EntityTickLimiter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
public class WindSpigotConfig {

	private static final Logger LOGGER = LogManager.getLogger(WindSpigotConfig.class);
	public static File CONFIG_FILE;

	private static final List<String> HEADER = new ArrayList<String>() {{
		add("This is the main configuration file for WindSpigot.");
		add("As you can see, there's tons to configure. Some options may impact gameplay, so use");
		add("with caution, and make sure you know what each option does before configuring.");
		add("If you need help with the configuration or have any questions related to WindSpigot,");
		add("join us in our Discord.");
		add("");
		add("");
		add("Discord: https://discord.gg/kAbTsFkbmN");
		add("Github: https://github.com/Wind-Development/WindSpigot");
	}};

	public static YamlConfiguration config;
	static int version;

	@Retention(RetentionPolicy.RUNTIME)
	private @interface Ignore {
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	public static void init(File configFile) {
		if (new File("nacho.yml").exists())
			new File("nacho.yml").renameTo(configFile);
		CONFIG_FILE = configFile;
		config = new YamlConfiguration();
		try {
			WindSpigot.LOGGER.info("Loading WindSpigot config from " + CONFIG_FILE.getName());
			config.load(CONFIG_FILE);
		} catch (IOException ignored) {
		} catch (InvalidConfigurationException ex) {
			LOGGER.log(Level.ERROR, "Could not load " + CONFIG_FILE.getName() + ", please correct your syntax errors", ex);
			throw Throwables.propagate(ex);
		}
		config.options().copyDefaults(true);
		config.options().setHeader(HEADER);
		config.options().parseComments(true);

		int configVersion = 28; // Update this every new configuration update

		version = getInt("config-version", configVersion);
		set("config-version", configVersion);
		readConfig(WindSpigotConfig.class, null);
	}

	@Ignore
	private static void cleanAndSave(boolean isWorldSettings) throws IOException {
		for (String key : config.getKeys(true)) {
			if (!config.getDefaults().contains(key) &&
					isWorldSettings ? !key.startsWith("settings.") : !key.startsWith("world-settings."))
				config.set(key, null);
		}
		config.save(CONFIG_FILE);
	}

	@Ignore
	private static void makeReadable() {
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
				if (method.getParameterTypes().length == 0 && method.getReturnType() == Void.TYPE && !method.isAnnotationPresent(Ignore.class)) {
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
			loadSpecialComments();
			cleanAndSave(instance != null);
		} catch (Exception ex) {
			LOGGER.log(Level.ERROR, "Could not save " + CONFIG_FILE.getName(), ex);
			makeReadable();
		}
	}

	@Ignore
	private static void loadSpecialComments() {
		setComment("config-version", "Configuration version, do NOT modify this!");
		setComment("settings.command", "Configuration for commands");
		setComment("settings.pearl-passthrough", "Configuration for ender pearls passing through certain blocks. (Credits to FlamePaper)");
		setComment("settings.spawners", "Configuration for MobSpawners");
		setComment("settings.max-tick-time", "Configuration for maximum entity tick time");

		// Async
		setComment("settings.async", "Configuration for actions made asynchronous");
		setComment("settings.async.path-searches", "Configuration for async entity path searches");
		setComment("settings.async.entity-tracking", "Configuration for the async entity tracker.");
		setComment("settings.async.explosions", "Configuration for async explosions");
	}

	private static void set(String path, Object val) {
		config.set(path, val);
	}

	static void setComment(String path, String comment) {
		List<String> lines;
		if (path.contains("\n"))
			lines = Arrays.asList(comment.split("\n"));
		else
			lines = Collections.singletonList(comment);

		config.setComments(path, lines);
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
		setComment("settings.async.entity-tracking.enable", "Enables asynchronous entity tracking");
		setComment("settings.async.entity-tracking.threads", "The amount of threads used for async entity tracking, increase or decrease this based on your server load.");
	}

	public static boolean threadAffinity;

	private static void threadAffinity() {
		threadAffinity = getBoolean("settings.thread-affinity", false);
		setComment("settings.thread-affinity", "Only switch to true if your OS is properly configured!! (See https://github.com/OpenHFT/Java-Thread-Affinity#isolcpus) \nWhen properly configured on the OS this allocates an entire cpu core to the server, it improves performance but uses more cpu.");
	}

	public static boolean mobAiCmd;

	private static void mobAiCmd() {
		mobAiCmd = getBoolean("settings.command.mob-ai", true);
		setComment("settings.command.mob-ai", "Enables the command \"/mobai\" which toggles mob ai. Users require the permission windspigot.command.mobai");
	}

	public static boolean limitedMobSpawns;

	private static void limitedMobSpawns() {
		limitedMobSpawns = getBoolean("settings.limited-mob-spawns", false);
		setComment("settings.limited-mob-spawns", "Disables mob spawning if TPS is lower than the specified threshold.");
	}

	public static double limitedMobSpawnsThreshold;

	private static void limitedMobSpawnsThreshold() {
		limitedMobSpawnsThreshold = getDouble("settings.limited-mob-spawns-threshold", 18);
		setComment("settings.limited-mob-spawns-threshold", "Threshold to disable mob spawning. This does not apply if limited mob spawns is not enabled. This option accepts decimals.");
	}

	// FlamePaper start - 0117-Pearl-through-blocks
	public static boolean pearlPassthroughFenceGate;
	public static boolean pearlPassthroughTripwire;
	public static boolean pearlPassthroughSlab;
	public static boolean pearlPassthroughCobweb;
	public static boolean pearlPassthroughBed;

	private static void pearlPassthrough() {
		pearlPassthroughFenceGate = getBoolean("settings.pearl-passthrough.fence-gate", false);
		setComment("settings.pearl-passthrough.fence-gate", "Allows pearls to pass through fences.");
		pearlPassthroughTripwire = getBoolean("settings.pearl-passthrough.tripwire", false);
		setComment("settings.pearl-passthrough.tripwire", "Allows pearls to pass through tripwires.");
		pearlPassthroughSlab = getBoolean("settings.pearl-passthrough.slab", false);
		setComment("settings.pearl-passthrough.slab", "Allows pearls to pass through slabs.");
		pearlPassthroughCobweb = getBoolean("settings.pearl-passthrough.cobweb", false);
		setComment("settings.pearl-passthrough.cobweb", "Allows pearls to pass through cobwebs.");
		pearlPassthroughBed = getBoolean("settings.pearl-passthrough.bed", false);
		setComment("settings.pearl-passthrough.bed", "Allows pearls to pass through beds.");
	}
	// FlamePaper end

	// From
	// https://github.com/Argarian-Network/NachoSpigot/tree/async-kb-hit
	public static int combatThreadTPS;

	private static void combatThread() {
		combatThreadTPS = getInt("settings.async.combat-thread-tps", 40);
		setComment("settings.async.combat-thread-tps", "Combat thread TPS for async knockback.");
	}

	// public static boolean asyncHitDetection;
	public static boolean asyncKnockback;

	private static void asyncPackets() {
		asyncKnockback = getBoolean("settings.async.knockback", false);
		setComment("settings.async.knockback", "Enables asynchronous knockback. This increases overall cpu usage, but sends knockback packets faster. Disable this if you do not run a pvp server. \nThis may be incompatible with a few plugins that listen to knockback packets. Test before using in production.");
	}

	public static boolean statistics;

	private static void statistics() {
		statistics = getBoolean("settings.statistics", true);
		setComment("settings.statistics", "Enables WindSpigot statistics. This allows developers to see how many WindSpigot servers are running. \nThis has no performance impact and is completely anonymous, but you can opt out of this if you want.");
	}

	public static int hitDelay;

	private static void hitDelay() {
		hitDelay = getInt("settings.hit-delay", 20);
		setComment("settings.hit-delay", "This sets the delay between player attacks, 20 is the default. Setting this to 0 allows for no hit delay.");
	}

	public static double potionSpeed;

	private static void potionSpeed() {
		potionSpeed = getDouble("settings.potion-speed-offset", 0);
		setComment("settings.potion-speed-offset", "This sets the speed offset of splash potions, 0 is the default speed. Setting this higher makes potions splash faster. \nThis config option accepts decimals.");
	}

	public static boolean showPlayerIps;

	private static void showPlayerIps() {
		showPlayerIps = getBoolean("settings.show-player-ips", true);
		setComment("settings.show-player-ips", "Disabling this will prevent display of player ips in the console.");
	}

	public static boolean modernKeepalive;

	private static void modernKeepalive() {
		modernKeepalive = getBoolean("settings.modern-keep-alive", false);
		setComment("settings.modern-keep-alive", "This enables keep alive handling from modern Minecraft. This may break some plugins.");
	}

	public static boolean asyncPathSearches;
	public static int distanceToAsync;
	public static int pathSearchThreads;
	public static boolean ensurePathSearchAccuracy;

	@SuppressWarnings("unchecked")
	private static void asyncPathSearches() {
		List<String> asyncSearchEntities = getList("settings.async.path-searches.entities",
				Lists.newArrayList("BAT", "BLAZE", "CHICKEN", "COW", "CREEPER", "ENDERMAN", "HORSE", "IRON_GOLEM",
						"MAGMA_CUBE", "MUSHROOM_COW", "PIG", "PIG_ZOMBIE", "RABBIT", "SHEEP", "SKELETON", "SILVERFISH",
						"SLIME", "SNOWMAN", "SQUID", "WITCH", "ZOMBIE"));
		setComment("settings.async.path-searches.entities", "A list of entities that utilize async path searches. Removing entities from this list will ensure 100% vanilla behavior, but worse performance.");

		asyncPathSearches = getBoolean("settings.async.path-searches.enabled", true);
		setComment("settings.async.path-searches.enabled", "Enables async path searching for entities.");

		distanceToAsync = getInt("settings.async.path-searches.distance-to-async", 0);
		setComment("settings.async.path-searches.distance-to-async", "The mininum distance an entity is targeting to handle it async. Tune this based on how many entities your server will has.");

		pathSearchThreads = getInt("settings.async.path-searches.threads", 4);
		setComment("settings.async.path-searches.threads", "The threads used for path searches. Tune this based on how many entities your server will has.");

		ensurePathSearchAccuracy = getBoolean("settings.async.path-searches.ensure-accuracy", true);
		setComment("settings.async.path-searches.ensure-accuracy", "Ensures accuracy of async path searches, disabling this will result in possibly inaccurate targeting, but higher performance.");

		if (asyncPathSearches) {
			List<EntityType> finalEntities = Lists.newArrayList();

			for (String entityName : asyncSearchEntities) {
				finalEntities.add(EntityType.fromName(entityName));
			}

			AsyncNavigation.addOffloadedEntities(finalEntities);
			AsyncNavigation.setMinimumDistanceForOffloading(distanceToAsync);
		}
	}

	public static boolean debugMode;

	private static void debugMode() {
		debugMode = getBoolean("settings.debug-mode", false);
		setComment("settings.debug-mode", "This outputs information to developers in the console. There is no need to enable this.");
	}

	public static int tileMaxTickTime;
	public static int entityMaxTickTime;

	private static void maxTickTimes() {
		entityMaxTickTime = getInt("settings.max-tick-time.entity", 35);
		setComment("settings.max-tick-time.entity", "The maximum time that entities can take to tick before moving on. This may break some gameplay, so set to 1000 to disable. \nFor reference, there are 50 ms in a tick.");
		tileMaxTickTime = 1000; // We do not re-implement the tile entity tick cap, so we disable it by setting it to 1000
	}

	public static boolean stopMobSpawnsDuringOverload;

	@SuppressWarnings({"unchecked", "deprecation"})
	private static void skippableEntities() {
		List<String> skippableEntities = getList("settings.max-tick-time.skippable-entities",
				Lists.newArrayList("BAT", "BLAZE", "CHICKEN", "COW", "CREEPER", "ENDERMAN", "HORSE", "IRON_GOLEM",
						"MAGMA_CUBE", "MUSHROOM_COW", "PIG", "PIG_ZOMBIE", "RABBIT", "SHEEP", "SKELETON", "SILVERFISH",
						"SLIME", "SNOWMAN", "SQUID", "WITCH", "ZOMBIE"));
		setComment("settings.max-tick-time.skippable-entities", "The entity types that can be skipped when ticking. They will only be skipped if the server is lagging based on the set threshold. \nRemove entities from this list if their vanilla behavior is absolutely needed on your server.");

		List<EntityType> finalEntities = Lists.newArrayList();

		for (String entityName : skippableEntities) {
			finalEntities.add(EntityType.fromName(entityName));
		}
		EntityTickLimiter.addSkippableEntities(finalEntities);

		stopMobSpawnsDuringOverload = getBoolean("settings.max-tick-time.limit-on-overload", false);
		setComment("settings.max-tick-time.limit-on-overload", "If the server should stop mob spawns when there are too many mobs to handle and some have to be skipped.");
	}

	public static boolean improvedHitDetection;

	private static void hitReg() {
		improvedHitDetection = getBoolean("settings.improved-hit-detection", true);
		setComment("settings.improved-hit-detection", "Enables the usage of an improved hit registration based on lag compensation and small other details. (Credits to NachoSpigot and the original plugin)");
	}

	public static boolean explosionAnimation;
	public static boolean explosionSounds;
	public static boolean spawnerAnimation;

	private static void particlesAndSounds() {
		explosionAnimation = getBoolean("settings.animation.tnt", true);
		setComment("settings.animation.tnt", "Enables explosion animations.");
		explosionSounds = getBoolean("settings.sound.tnt", true);
		setComment("settings.sound.tnt", "Enables explosion sounds.");
		spawnerAnimation = getBoolean("settings.animation.spawner", true);
		setComment("settings.animation.spawner", "Enables mob spawner particles.");
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
		setComment("settings.spawners.initial-spawn-delay", "Initial spawn delay");
		spawnersMinSpawnDelay = getInt("settings.spawners.min-spawn-delay", 200);
		setComment("settings.spawners.min-spawn-delay", "Minimum spawn delay");
		spawnersMaxSpawnDelay = getInt("settings.spawners.max-spawn-delay", 800);
		setComment("settings.spawners.max-spawn-delay", "Maximum spawn delay");
		spawnersSpawnCount = getInt("settings.spawners.spawn-count", 4);
		setComment("settings.spawners.spawn-count", "Max amount of entities that can be spawned");
		spawnersSpawnRange = getInt("settings.spawners.spawn-range", 4);
		setComment("settings.spawners.spawn-range", "Maximum area in which entities spawn");
		spawnersMaxNearbyEntities = getInt("settings.spawners.max-nearby-entities", 6);
		setComment("settings.spawners.max-nearby-entities", "Maximum number of nearby entities to avoid new spawns (Any value lower than 1 will disable this check)");
		spawnersRequiredPlayerRange = getInt("settings.spawners.required-player-range", 16);
		setComment("settings.spawners.required-player-range", "Area in which the player is required to spawn entities");
	}

	public static boolean weatherChange;

	private static void weatherChange() {
		weatherChange = getBoolean("settings.weather-change", true);
		setComment("settings.weather-change", "Enables changing of weather.");
	}

	public static boolean asyncTnt;
	public static int fixedPoolSize;

	private static void asyncExplosions() {
		asyncTnt = getBoolean("settings.async.explosions.enable", true);
		setComment("settings.async.explosions.enable", "Enables async explosion calculatons.");
		fixedPoolSize = getInt("settings.async.explosions.threads", 3);
		setComment("settings.async.explosions.threads", "The threads used for async explosions");
	}

	public static double maxReachSqrd;
	public static boolean creativeBypass;

	private static void maxReach() {
		maxReachSqrd = Math.pow(getDouble("settings.max-reach.value", 6), 2);
		setComment("settings.max-reach.value", "The maximum reach a player can use, hits farther than this will be cancelled. Players can reach 6 blocks by default.");
		creativeBypass = getBoolean("settings.max-reach.creative-bypass", true);
		setComment("settings.max-reach.creative-bypass", "If creative players are immune to this reach cap");
	}

	public static double fishingRodMultiplier;

	private static void fishingRodMultiplier() {
		fishingRodMultiplier = getDouble("settings.fishing-rod-multiplier", 1);
		setComment("settings.fishing-rod-multiplier", "The speed multiplier of fishing rod projectiles.");
	}

	// Below are NachoSpigot config options
	public static boolean saveEmptyScoreboardTeams;

	private static void saveEmptyScoreboardTeams() {
		saveEmptyScoreboardTeams = getBoolean("settings.save-empty-scoreboard-teams", false);
	}

	public static boolean enableVersionCommand;
	public static boolean enablePluginsCommand;
	public static boolean enableReloadCommand;
	public static boolean pingCmd;
	public static String pingSelfCmdString;
	public static String pingOtherCmdString;

	private static void commands() {
		// Migration
		if (config.contains("settings.commands.enable-version-command"))
			set("settings.command.version", config.get("settings.commands.enable-version-command"));
		if (config.contains("settings.commands.enable-plugins-command"))
			set("settings.command.plugins", config.get("settings.commands.enable-plugins-command"));
		if (config.contains("settings.commands.enable-reload-command"))
			set("settings.command.reload", config.get("settings.commands.enable-reload-command"));

		// Basic command configs
		enableVersionCommand = getBoolean("settings.command.version", true);
		setComment("settings.command.version", "Enables the /version command");
		enablePluginsCommand = getBoolean("settings.command.plugins", true);
		setComment("settings.command.plugins", "Enables the /plugins command");
		enableReloadCommand = getBoolean("settings.command.reload", false);
		setComment("settings.command.reload", "Enables the /reload command (It is recommended to not use /reload)");

		// Ping
		pingCmd = getBoolean("settings.command.ping.enable", true);
		setComment("settings.command.ping.enable", "Enables the command \"/ping <player>\" which shows player ping. Users require the permission windspigot.command.ping");
		pingSelfCmdString = getString("settings.command.ping.self-ping-msg", "&bYour ping: &3%ping%");
		setComment("settings.command.ping.self-ping-msg", "The message displayed for the /ping command");
		pingOtherCmdString = getString("settings.command.ping.other-ping-msg", "&3%player%'s &bping: &3%ping%");
		setComment("settings.command.ping.other-ping-msg", "The message displayed for the /ping <player> command");
	}

	public static boolean useFastOperators;

	private static void useFastOperators() {
		useFastOperators = getBoolean("settings.fast-operators", false);
		setComment("settings.fast-operators", "Disables storage of operators, which deops all operators on server restarts");
	}

	public static boolean patchProtocolLib;

	private static void patchProtocolLib() {
		patchProtocolLib = getBoolean("settings.patch-protocollib", true);
		setComment("settings.save-empty-scoreboard-teams", "Toggles whether or not the server should save empty scoreboard teams");
	}

	public static boolean stopNotifyBungee;

	private static void stopNotifyBungee() {
		stopNotifyBungee = getBoolean("settings.stop-notify-bungee", false);
		setComment("settings.stop-notify-bungee", "Disables the firewall check when running BungeeCord");
	}

	public static boolean checkForMalware;

	private static void antiMalware() {
		checkForMalware = getBoolean("settings.anti-malware", true);
		setComment("settings.anti-malware", "Enables the built-in anti malware feature");
	}

	public static boolean kickOnIllegalBehavior;

	private static void kickOnIllegalBehavior() {
		kickOnIllegalBehavior = getBoolean("settings.kick-on-illegal-behavior", true);
		setComment("settings.kick-on-illegal-behavior", "Kicks players if they try to do an illegal action (e.g. using a creative mode action while not in creative mode.)");
	}

	public static boolean usePandaWire;

	private static void usePandaWire() {
		usePandaWire = getBoolean("settings.panda-wire", true);
		setComment("settings.panda-wire", "Optimizes redstone wires.");
	}

	public static boolean fireEntityExplodeEvent;
	public static boolean firePlayerMoveEvent;
	public static boolean leavesDecayEvent;

	private static void fireEntityExplodeEvent() {
		fireEntityExplodeEvent = getBoolean("settings.event.fire-entity-explode-event", true);
		setComment("settings.event.fire-entity-explode-event", "Enables the entity explode event.");
		firePlayerMoveEvent = getBoolean("settings.event.fire-player-move-event", true);
		setComment("settings.event.fire-player-move-event", "Enables the player move event.");
		leavesDecayEvent = getBoolean("settings.event.fire-leaf-decay-event", true);
		setComment("settings.event.fire-leaf-decay-event", "Enables the leaf decay event.");
	}

	public static String serverBrandName;

	private static void serverBrandName() {
		if (config.getString("settings.brand-name").equals("NachoSpigot"))
			set("settings.brand-name", "WindSpigot");
		serverBrandName = getString("settings.brand-name", "WindSpigot");
		setComment("settings.brand-name", "Changes the brand name of the server.\nThis will show in statistics, server lists, client crashes, and in the client debug screen. (accessed by pressing F3)");
	}

	public static boolean stopDecodingItemStackOnPlace;

	private static void stopDecodingItemStackOnPlace() {
		stopDecodingItemStackOnPlace = getBoolean("settings.stop-decoding-itemstack-on-place", true);
		setComment("settings.stop-decoding-itemstack-on-place", "Disables decoding itemstacks when not needed.");
	}

	public static boolean enableAntiCrash;

	private static void enableAntiCrash() {
		if (config.contains("settings.anti-crash"))
			set("settings.anti-crash.enabled", config.get("settings.anti-crash"));
		enableAntiCrash = getBoolean("settings.anti-crash.enabled", true);
		setComment("settings.anti-crash.enabled", "Kicks players if they try to do an action that could crash the server.");
	}

	public static int chunkThreads; // PaperSpigot - Bumped value
	public static int playersPerThread;

	private static void chunk() {
		chunkThreads = getInt("settings.chunk.threads", 2);
		setComment("settings.chunk.threads", "The amount of threads used for chunks.");
		playersPerThread = getInt("settings.chunk.players-per-thread", 50);
		setComment("settings.chunk.players-per-thread", "The amount of players for each thread.");
	}

	public static boolean enableTCPNODELAY;

	private static void enableTCPNODELAY() {
		enableTCPNODELAY = getBoolean("settings.use-tcp-nodelay", true);
		setComment("settings.use-tcp-nodelay", "Enables the TCP_NODELAY socket option.");
	}

	public static boolean useFasterCannonTracker;

	private static void useFasterCannonTracker() {
		useFasterCannonTracker = getBoolean("settings.faster-cannon-tracker", true);
		setComment("settings.faster-cannon-tracker", "Enables a faster cannon entity tracker.");
	}

	public static boolean fixEatWhileRunning;

	private static void fixEatWhileRunning() {
		fixEatWhileRunning = getBoolean("settings.fix-eat-while-running", true);
		setComment("settings.fix-eat-while-running", "Fixes the eating while running bug.");
	}

	public static boolean hideProjectilesFromHiddenPlayers;

	private static void hideProjectilesFromHiddenPlayers() {
		hideProjectilesFromHiddenPlayers = getBoolean("settings.hide-projectiles-from-hidden-players", false);
		setComment("settings.hide-projectiles-from-hidden-players", "Hides projectiles from hidden players.");
	}

	public static boolean lagCompensatedPotions;

	private static void lagCompensatedPotions() {
		lagCompensatedPotions = getBoolean("settings.lag-compensated-potions", true);
		setComment("settings.lag-compensated-potions", "Enables lag compesation for thrown potions.");
	}

	public static boolean smoothPotting;

	private static void smoothPotting() {
		smoothPotting = getBoolean("settings.smooth-potting", true);
		setComment("settings.smooth-potting", "Makes potion throwing smoother.");
	}

	public static boolean antiEnderPearlGlitch;

	private static void antiEnderPearlGlitch() {
		antiEnderPearlGlitch = getBoolean("settings.anti-enderpearl-glitch", true);
		setComment("settings.anti-enderpearl-glitch", "Blocks enderpearl glitching.");
	}

	public static boolean disabledFallBlockAnimation;

	private static void disableFallAnimation() {
		if (config.contains("settings.disabled-block-fall-animation"))
			set("settings.disable-block-fall-animation", config.get("settings.disabled-block-fall-animation"));
		disabledFallBlockAnimation = getBoolean("settings.disable-block-fall-animation", false);
		setComment("settings.disable-block-fall-animation", "Disables the fall animation for blocks.");
	}

	public static boolean disableInfiniSleeperThreadUsage;

	private static void disableInfiniSleeperThreadUsage() {
		disableInfiniSleeperThreadUsage = getBoolean("settings.disable-infinisleeper-thread-usage", false);
		setComment("settings.disable-infinisleeper-thread-usage", "Disable infinisleeper thread usage, only enable this if you know what are you doing.");
	}

	public static int itemDirtyTicks;

	private static void itemDirtyTicks() {
		itemDirtyTicks = getInt("settings.item-dirty-ticks", 20);
		setComment("settings.item-dirty-ticks", "Controls the interval for the item-dirty check. Minecraft checks an item every tick to see if it was changed. This can be expensive because it also needs to check all NBT data. Spigot only checks for basic count/data/type data and does a deep check every 20 ticks by default.");
	}

	public static boolean enableTcpFastOpen;
	public static int modeTcpFastOpen;

	private static void fastOpen() {
		if (config.contains("settings.use-tcp-fastopen"))
			set("settings.tcp-fast-open.enabled", config.get("settings.use-tcp-fastopen"));
		if (config.contains("settings.tcp-fastopen-mode"))
			set("settings.tcp-fast-open.mode", config.get("settings.tcp-fastopen-mode"));
		enableTcpFastOpen = getBoolean("settings.tcp-fast-open.enabled", true);
		setComment("settings.tcp-fast-open.enabled", "Enables the TCP_FASTOPEN socket option.");
		modeTcpFastOpen = getInt("settings.tcp-fast-open.mode", 1);
		setComment("settings.tcp-fast-open.mode", "Options: 0 - Disabled.; 1 - TFO is enabled for outgoing connections (clients).; 2 - TFO is enabled for incoming connections (servers).; 3 - TFO is enabled for both clients and servers.");
	}

	public static boolean enableProtocolLibShim;

	private static void enableProtocolLibShim() {
		enableProtocolLibShim = getBoolean("settings.enable-protocollib-shim", true);
		setComment("settings.enable-protocollib-shim", "Enable ProtocolLib network shim. This allows ProtocolLib to work, but requires extra memory. Disable this if you don't use ProtocolLib!");
	}

	public static boolean instantPlayInUseEntity;

	private static void instantPlayInUseEntity() {
		instantPlayInUseEntity = getBoolean("settings.instant-interaction", false);
		setComment("settings.instant-interaction", "Disables delay of all interactions.");
	}
}
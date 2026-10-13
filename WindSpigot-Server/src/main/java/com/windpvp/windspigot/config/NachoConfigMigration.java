package com.windpvp.windspigot.config;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Migrates overlapping NachoSpigot ({@code nacho.yml}) options into WindSpigot
 * ({@code windspigot.yml}), including persistence of YAML keys — not only in-memory fields.
 */
public final class NachoConfigMigration {

	public static final String MIGRATED_FLAG_PATH = "settings.migrated-from-nacho";

	/**
	 * Nacho public static field name → WindSpigot YAML path.
	 * Intentionally omits options removed from WindSpigot (ProtocolLib shim, item-stack decode, etc.).
	 */
	private static final Map<String, String> FIELD_TO_WIND_PATH;

	/** Fields that exist on NachoConfig but must not be migrated (removed or infrastructure). */
	private static final Set<String> SKIP_FIELDS;

	static {
		Map<String, String> paths = new LinkedHashMap<String, String>();
		paths.put("saveEmptyScoreboardTeams", "settings.save-empty-scoreboard-teams");
		paths.put("enableVersionCommand", "settings.command.version");
		paths.put("enablePluginsCommand", "settings.command.plugins");
		paths.put("enableReloadCommand", "settings.command.reload");
		paths.put("useFastOperators", "settings.fast-operators");
		paths.put("stopNotifyBungee", "settings.stop-notify-bungee");
		paths.put("checkForMalware", "settings.anti-malware");
		paths.put("kickOnIllegalBehavior", "settings.kick-on-illegal-behavior");
		paths.put("usePandaWire", "settings.panda-wire");
		paths.put("fireEntityExplodeEvent", "settings.event.fire-entity-explode-event");
		paths.put("firePlayerMoveEvent", "settings.event.fire-player-move-event");
		paths.put("leavesDecayEvent", "settings.event.fire-leaf-decay-event");
		paths.put("serverBrandName", "settings.brand-name");
		paths.put("enableAntiCrash", "settings.anti-crash.enabled");
		paths.put("chunkThreads", "settings.chunk.threads");
		paths.put("playersPerThread", "settings.chunk.players-per-thread");
		paths.put("enableTCPNODELAY", "settings.use-tcp-nodelay");
		paths.put("fixedPoolSize", "settings.async.explosions.threads");
		paths.put("useFasterCannonTracker", "settings.faster-cannon-tracker");
		paths.put("fixEatWhileRunning", "settings.fix-eat-while-running");
		paths.put("hideProjectilesFromHiddenPlayers", "settings.hide-projectiles-from-hidden-players");
		paths.put("lagCompensatedPotions", "settings.lag-compensated-potions");
		paths.put("smoothPotting", "settings.smooth-potting");
		paths.put("antiEnderPearlGlitch", "settings.anti-enderpearl-glitch");
		paths.put("disabledFallBlockAnimation", "settings.disable-block-fall-animation");
		paths.put("disableInfiniSleeperThreadUsage", "settings.disable-infinisleeper-thread-usage");
		paths.put("enableTcpFastOpen", "settings.tcp-fast-open.enabled");
		paths.put("modeTcpFastOpen", "settings.tcp-fast-open.mode");
		paths.put("instantPlayInUseEntity", "settings.instant-interaction");
		FIELD_TO_WIND_PATH = Collections.unmodifiableMap(paths);

		Set<String> skip = new LinkedHashSet<String>();
		skip.add("CONFIG_FILE");
		skip.add("hasMigrated");
		// Removed from WindSpigot / no longer wired into runtime
		skip.add("patchProtocolLib");
		skip.add("stopDecodingItemStackOnPlace");
		skip.add("itemDirtyTicks");
		skip.add("enableProtocolLibShim");
		SKIP_FIELDS = Collections.unmodifiableSet(skip);
	}

	private NachoConfigMigration() {
	}

	public static Map<String, String> fieldToWindPath() {
		return FIELD_TO_WIND_PATH;
	}

	public static Set<String> skipFields() {
		return SKIP_FIELDS;
	}

	public static String windPathFor(String fieldName) {
		return FIELD_TO_WIND_PATH.get(fieldName);
	}

	/**
	 * Writes migrated option values into a YAML sink (path → value).
	 *
	 * @return number of options written
	 */
	public static int applyToYaml(Map<String, Object> fieldValues, BiConsumer<String, Object> yamlSink) {
		int written = 0;
		for (Map.Entry<String, Object> entry : fieldValues.entrySet()) {
			String path = FIELD_TO_WIND_PATH.get(entry.getKey());
			if (path != null) {
				yamlSink.accept(path, entry.getValue());
				written++;
			}
		}
		return written;
	}

	/**
	 * Copies overlapping public static fields from {@code nachoClass} onto
	 * {@code windClass}, writes matching keys into {@code windYaml}, and copies
	 * {@code world-settings} so world options are not dropped when {@code nacho.yml}
	 * is later removed.
	 *
	 * @return number of overlapping fields migrated
	 */
	public static int migrate(Class<?> nachoClass, Class<?> windClass, YamlConfiguration nachoYaml,
			YamlConfiguration windYaml, BiConsumer<String, String> warn) throws IllegalAccessException {
		Map<String, Object> values = new LinkedHashMap<String, Object>();
		int migrated = 0;

		for (Field oldField : nachoClass.getDeclaredFields()) {
			if (!Modifier.isPublic(oldField.getModifiers()) || !Modifier.isStatic(oldField.getModifiers())) {
				continue;
			}
			String name = oldField.getName();
			if (SKIP_FIELDS.contains(name)) {
				continue;
			}
			if (!FIELD_TO_WIND_PATH.containsKey(name)) {
				if (warn != null) {
					warn.accept(name, "no WindSpigot mapping (left on nacho.yml / ignored)");
				}
				continue;
			}

			oldField.setAccessible(true);
			Object value = oldField.get(null);
			values.put(name, value);

			Field newField = findPublicStaticField(windClass, name);
			if (newField == null) {
				if (warn != null) {
					warn.accept(name, "missing matching WindSpigotConfig field");
				}
				continue;
			}
			newField.setAccessible(true);
			newField.set(null, value);
			migrated++;
		}

		applyToYaml(values, new BiConsumer<String, Object>() {
			@Override
			public void accept(String path, Object value) {
				windYaml.set(path, value);
			}
		});

		copyWorldSettings(nachoYaml, windYaml);
		windYaml.set(MIGRATED_FLAG_PATH, true);
		return migrated;
	}

	static void copyWorldSettings(YamlConfiguration nachoYaml, YamlConfiguration windYaml) {
		if (nachoYaml == null || windYaml == null) {
			return;
		}
		ConfigurationSection worldSettings = nachoYaml.getConfigurationSection("world-settings");
		if (worldSettings == null) {
			return;
		}
		for (String key : worldSettings.getKeys(true)) {
			if (worldSettings.isConfigurationSection(key)) {
				continue;
			}
			windYaml.set("world-settings." + key, worldSettings.get(key));
		}
	}

	private static Field findPublicStaticField(Class<?> clazz, String name) {
		try {
			Field field = clazz.getDeclaredField(name);
			if (Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) {
				return field;
			}
		} catch (NoSuchFieldException ignored) {
		}
		return null;
	}
}

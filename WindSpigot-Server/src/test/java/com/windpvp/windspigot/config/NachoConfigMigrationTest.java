package com.windpvp.windspigot.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Assert;
import org.junit.Test;

/**
 * Verifies nacho.yml → windspigot.yml path mapping and YAML persistence helpers.
 */
public class NachoConfigMigrationTest {

	@Test
	public void windPathsCoverSharedRuntimeOptions() {
		String[] required = new String[] {
				"saveEmptyScoreboardTeams",
				"enableVersionCommand",
				"enablePluginsCommand",
				"enableReloadCommand",
				"useFastOperators",
				"stopNotifyBungee",
				"checkForMalware",
				"kickOnIllegalBehavior",
				"usePandaWire",
				"fireEntityExplodeEvent",
				"firePlayerMoveEvent",
				"leavesDecayEvent",
				"serverBrandName",
				"enableAntiCrash",
				"chunkThreads",
				"playersPerThread",
				"enableTCPNODELAY",
				"fixedPoolSize",
				"useFasterCannonTracker",
				"fixEatWhileRunning",
				"hideProjectilesFromHiddenPlayers",
				"lagCompensatedPotions",
				"smoothPotting",
				"antiEnderPearlGlitch",
				"disabledFallBlockAnimation",
				"disableInfiniSleeperThreadUsage",
				"enableTcpFastOpen",
				"modeTcpFastOpen",
				"instantPlayInUseEntity"
		};
		for (String field : required) {
			Assert.assertNotNull("Missing Wind path for " + field, NachoConfigMigration.windPathFor(field));
		}
	}

	@Test
	public void removedNachoOptionsAreNotMigrated() {
		Assert.assertNull(NachoConfigMigration.windPathFor("patchProtocolLib"));
		Assert.assertNull(NachoConfigMigration.windPathFor("stopDecodingItemStackOnPlace"));
		Assert.assertNull(NachoConfigMigration.windPathFor("itemDirtyTicks"));
		Assert.assertNull(NachoConfigMigration.windPathFor("enableProtocolLibShim"));
		Assert.assertTrue(NachoConfigMigration.skipFields().contains("patchProtocolLib"));
		Assert.assertTrue(NachoConfigMigration.skipFields().contains("itemDirtyTicks"));
	}

	@Test
	public void applyToYamlUsesWindPathsNotNachoPaths() {
		Map<String, Object> values = new LinkedHashMap<String, Object>();
		values.put("enableAntiCrash", Boolean.FALSE);
		values.put("fixedPoolSize", Integer.valueOf(7));
		values.put("enableVersionCommand", Boolean.FALSE);
		values.put("disabledFallBlockAnimation", Boolean.TRUE);
		values.put("enableTcpFastOpen", Boolean.FALSE);
		values.put("modeTcpFastOpen", Integer.valueOf(3));
		// Intentionally-removed option must be ignored
		values.put("patchProtocolLib", Boolean.FALSE);

		Map<String, Object> yaml = new LinkedHashMap<String, Object>();
		int written = NachoConfigMigration.applyToYaml(values, yaml::put);

		Assert.assertEquals(6, written);
		Assert.assertEquals(Boolean.FALSE, yaml.get("settings.anti-crash.enabled"));
		Assert.assertEquals(Integer.valueOf(7), yaml.get("settings.async.explosions.threads"));
		Assert.assertEquals(Boolean.FALSE, yaml.get("settings.command.version"));
		Assert.assertEquals(Boolean.TRUE, yaml.get("settings.disable-block-fall-animation"));
		Assert.assertEquals(Boolean.FALSE, yaml.get("settings.tcp-fast-open.enabled"));
		Assert.assertEquals(Integer.valueOf(3), yaml.get("settings.tcp-fast-open.mode"));
		Assert.assertFalse(yaml.containsKey("settings.anti-crash"));
		Assert.assertFalse(yaml.containsKey("settings.fixed-pools.size"));
		Assert.assertFalse(yaml.containsKey("settings.commands.enable-version-command"));
		Assert.assertFalse(yaml.containsKey("settings.patch-protocollib"));
	}

	@Test
	public void copyWorldSettingsPersistsIntoWindYaml() {
		YamlConfiguration nacho = new YamlConfiguration();
		nacho.set("world-settings.default.entity.mob-ai", Boolean.FALSE);
		nacho.set("world-settings.default.explosions.reduced-density-rays", Boolean.FALSE);
		nacho.set("world-settings.world.entity.mob-ai", Boolean.TRUE);

		YamlConfiguration wind = new YamlConfiguration();
		NachoConfigMigration.copyWorldSettings(nacho, wind);

		Assert.assertEquals(Boolean.FALSE, wind.get("world-settings.default.entity.mob-ai"));
		Assert.assertEquals(Boolean.FALSE, wind.get("world-settings.default.explosions.reduced-density-rays"));
		Assert.assertEquals(Boolean.TRUE, wind.get("world-settings.world.entity.mob-ai"));
	}

	@Test
	public void migratedFlagPathIsStable() {
		Assert.assertEquals("settings.migrated-from-nacho", NachoConfigMigration.MIGRATED_FLAG_PATH);
	}
}

package com.windpvp.windspigot.citizens;

import com.windpvp.windspigot.commons.PluginUtils;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class CitizensVersionDetectionTest {

    @Test
    public void testOlderBuildDetected() {
        Plugin plugin = Mockito.mock(Plugin.class);
        PluginDescriptionFile desc = new PluginDescriptionFile("Citizens", "2.0.25-SNAPSHOT (build 1770)", "net.citizensnpcs.Citizens");
        Mockito.when(plugin.getDescription()).thenReturn(desc);

        int build = PluginUtils.getCitizensBuild(plugin);
        Assert.assertEquals(1770, build);
        Assert.assertTrue("Older build 1770 must be less than 2396", build < 2396);
    }

    @Test
    public void testNewerBuildDetected() {
        Plugin plugin = Mockito.mock(Plugin.class);
        PluginDescriptionFile desc = new PluginDescriptionFile("Citizens", "2.0.28-SNAPSHOT (build 2400)", "net.citizensnpcs.Citizens");
        Mockito.when(plugin.getDescription()).thenReturn(desc);

        int build = PluginUtils.getCitizensBuild(plugin);
        Assert.assertEquals(2400, build);
        Assert.assertTrue("Newer build 2400 must be >= 2396", build >= 2396);
    }

    @Test
    public void testFallbackOnUnknownVersion() {
        Plugin plugin = Mockito.mock(Plugin.class);
        PluginDescriptionFile desc = new PluginDescriptionFile("Citizens", "unknown-version", "net.citizensnpcs.Citizens");
        Mockito.when(plugin.getDescription()).thenReturn(desc);

        int build = PluginUtils.getCitizensBuild(plugin);
        Assert.assertEquals(2396, build);
    }
}

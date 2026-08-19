package com.windpvp.windspigot.citizens;

import com.windpvp.windspigot.commons.PluginUtils;
import org.junit.Assert;
import org.junit.Test;

public class CitizensVersionDetectionTest {

    @Test
    public void testOlderBuildDetected() {
        int build = PluginUtils.parseCitizensBuild("2.0.25-SNAPSHOT (build 1770)");
        Assert.assertEquals(1770, build);
        Assert.assertTrue("Older build 1770 must be less than 2396", build < 2396);
    }

    @Test
    public void testLegacyVersionWithoutBuildDetected() {
        int build25 = PluginUtils.parseCitizensBuild("2.0.25");
        Assert.assertTrue("2.0.25 must trigger older build detection", build25 < 2396);

        int build26 = PluginUtils.parseCitizensBuild("2.0.26-SNAPSHOT");
        Assert.assertTrue("2.0.26 must trigger older build detection", build26 < 2396);
    }

    @Test
    public void testNewerBuildDetected() {
        int build = PluginUtils.parseCitizensBuild("2.0.28-SNAPSHOT (build 2400)");
        Assert.assertEquals(2400, build);
        Assert.assertTrue("Newer build 2400 must be >= 2396", build >= 2396);
    }

    @Test
    public void testFallbackOnNullOrUnknownVersion() {
        Assert.assertEquals(2396, PluginUtils.parseCitizensBuild(null));
        Assert.assertEquals(2396, PluginUtils.parseCitizensBuild("unknown-version"));
        Assert.assertEquals(2396, PluginUtils.getCitizensBuild(null));
    }
}

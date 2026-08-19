package org.spigotmc;

import org.junit.Assert;
import org.junit.Test;

public class JavaVersionParsingRegressionTest {

    private int parseJavaMajorVersion(String version) {
        if (version == null) {
            return 8;
        }
        if (version.startsWith("1.")) {
            if (version.length() >= 3) {
                try {
                    return Integer.parseInt(version.substring(2, 3));
                } catch (NumberFormatException ignored) {
                }
            }
            return 8;
        }
        String parsedVersion = version;
        int dot = parsedVersion.indexOf(".");
        if (dot != -1) {
            parsedVersion = parsedVersion.substring(0, dot);
        }
        int dash = parsedVersion.indexOf("-");
        if (dash != -1) {
            parsedVersion = parsedVersion.substring(0, dash);
        }
        try {
            return Integer.parseInt(parsedVersion);
        } catch (NumberFormatException e) {
            return 8;
        }
    }

    @Test
    public void testJava8Version() {
        Assert.assertEquals(8, parseJavaMajorVersion("1.8.0_312"));
        Assert.assertEquals(8, parseJavaMajorVersion("1.8.0_202"));
    }

    @Test
    public void testJava9PlusVersions() {
        Assert.assertEquals(9, parseJavaMajorVersion("9.0.4"));
        Assert.assertEquals(11, parseJavaMajorVersion("11.0.18"));
        Assert.assertEquals(17, parseJavaMajorVersion("17.0.2"));
        Assert.assertEquals(21, parseJavaMajorVersion("21.0.1"));
        Assert.assertEquals(25, parseJavaMajorVersion("25-ea"));
    }
}

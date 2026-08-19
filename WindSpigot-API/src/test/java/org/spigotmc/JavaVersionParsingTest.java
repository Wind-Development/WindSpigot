package org.spigotmc;

import org.junit.Assert;
import org.junit.Test;

/**
 * JavaVersionParsingTest: Verify version detection across Java 8, 11, 17, 21, 25.
 */
public class JavaVersionParsingTest {

    private int getJavaMajorVersion(String version) {
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
    public void testJava8VersionStrings() {
        Assert.assertEquals(8, getJavaMajorVersion("1.8.0_312"));
        Assert.assertEquals(8, getJavaMajorVersion("1.8.0_202-b08"));
        Assert.assertEquals(8, getJavaMajorVersion("1.8.0"));
    }

    @Test
    public void testJava9PlusVersionStrings() {
        Assert.assertEquals(9, getJavaMajorVersion("9.0.4"));
        Assert.assertEquals(11, getJavaMajorVersion("11.0.18"));
        Assert.assertEquals(17, getJavaMajorVersion("17.0.2"));
        Assert.assertEquals(21, getJavaMajorVersion("21.0.1"));
        Assert.assertEquals(25, getJavaMajorVersion("25-ea"));
        Assert.assertEquals(25, getJavaMajorVersion("25.0.0"));
    }

    @Test
    public void testFallbackOnNullOrMalformed() {
        Assert.assertEquals(8, getJavaMajorVersion(null));
        Assert.assertEquals(8, getJavaMajorVersion("unknown"));
    }
}

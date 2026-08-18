package com.windpvp.windspigot.commons;

import org.bukkit.plugin.Plugin;

public class PluginUtils {

    public static int getCitizensBuild(Plugin plugin) {
        if (plugin == null || plugin.getDescription() == null) {
            return 2396;
        }
        return parseCitizensBuild(plugin.getDescription().getVersion());
    }

    public static int parseCitizensBuild(String version) {
        try {
            if (version == null) {
                return 2396;
            }
            if (version.contains("(build ")) {
                return Integer.parseInt(version.split("\\(build ")[1].replace(")", "").trim());
            }
            if (version.startsWith("2.0.25") || version.startsWith("2.0.26") || version.startsWith("2.0.27")) {
                return 1000;
            }
            return 2396;
        } catch (Throwable ignored) {
            return 2396;
        }
    }
}
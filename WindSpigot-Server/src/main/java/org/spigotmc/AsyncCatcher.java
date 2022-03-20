package org.spigotmc;

import ga.windpvp.windspigot.config.WindSpigotConfig;
import net.minecraft.server.MinecraftServer;

public class AsyncCatcher {

	public static boolean enabled = true;

	public static void catchOp(String reason) {
		if (!WindSpigotConfig.parallelWorld && (enabled && Thread.currentThread() != MinecraftServer.getServer().primaryThread)) {
			throw new IllegalStateException("Asynchronous " + reason + "!");
		}
	}
}

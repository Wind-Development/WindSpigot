package dev.cobblesword.nachospigot.commons;

import net.minecraft.server.MinecraftServer;

@Deprecated
public class MCUtils {
	public static void ensureMain(Runnable runnable) {
		MinecraftServer.getServer().processQueue.add(runnable);
	}
}

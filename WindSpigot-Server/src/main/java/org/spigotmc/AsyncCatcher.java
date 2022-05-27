package org.spigotmc;

import org.bukkit.Bukkit;

public class AsyncCatcher {

	public static boolean enabled = true;

	public static void catchOp(String reason) {
		if (enabled && !Bukkit.isPrimaryThread()) {
			throw new IllegalStateException("Asynchronous " + reason + "!");
		}
	}
}

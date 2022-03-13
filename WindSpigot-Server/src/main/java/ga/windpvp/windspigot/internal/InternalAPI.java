package ga.windpvp.windspigot.internal;

import org.bukkit.ChatColor;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldServer;

// API not meant for plugin usage
public class InternalAPI {

	public static boolean aiEnabled;

	public static String enableAi() {
		String msg = null;
		for (WorldServer world : MinecraftServer.getServer().worlds) {
			world.nachoSpigotConfig.enableMobAI = true;
			msg = ChatColor.AQUA + "Mob AI enabled in all worlds.";

		}
		aiEnabled = true;
		return msg;
	}

	public static String disableAi() {
		String msg = null;
		for (WorldServer world : MinecraftServer.getServer().worlds) {
			world.nachoSpigotConfig.enableMobAI = false;
			msg = ChatColor.AQUA + "Mob AI disabled in all worlds.";

		}
		aiEnabled = false;
		return msg;
	}

	public static String toggleAi() {
		String msg = null;
		
		boolean setData = true;
		
		for (WorldServer world : MinecraftServer.getServer().worlds) {
			if (world.nachoSpigotConfig.enableMobAI) {
				world.nachoSpigotConfig.enableMobAI = false;
				if (setData) {
					msg = ChatColor.AQUA + "Mob AI disabled in all worlds.";
					aiEnabled = false;
					setData = false;
				}
			} else {
				world.nachoSpigotConfig.enableMobAI = true;
				if (setData) {
					msg = ChatColor.AQUA + "Mob AI enabled in all worlds.";
					aiEnabled = true;
					setData = false;
				}
			}
		}

		return msg;
	}

}

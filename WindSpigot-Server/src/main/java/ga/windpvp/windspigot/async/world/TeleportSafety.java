package ga.windpvp.windspigot.async.world;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.server.WorldServer;

public class TeleportSafety {

	public static Map<WorldServer, Boolean> isWaitingOnTeleport;

	public static void init() {
		if (isWaitingOnTeleport == null) {
			isWaitingOnTeleport = Maps.newConcurrentMap();
		}
	}

}

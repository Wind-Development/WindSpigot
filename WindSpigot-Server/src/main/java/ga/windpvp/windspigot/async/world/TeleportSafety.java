package ga.windpvp.windspigot.async.world;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.server.WorldServer;

public class TeleportSafety {
	
	public static final Map<WorldServer, Boolean> isWaitingOnTeleport = Maps.newConcurrentMap();

}

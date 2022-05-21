package ga.windpvp.windspigot.protocol;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.minecraft.server.PacketPlayInFlying;

public interface MovementListener {
	default boolean updateLocation(Player paramPlayer, Location paramLocation1, Location paramLocation2,
			PacketPlayInFlying paramPacketPlayInFlying) {
		return true;
	}

	default boolean updateRotation(Player paramPlayer, Location paramLocation1, Location paramLocation2,
			PacketPlayInFlying paramPacketPlayInFlying) {
		return true;
	}
}

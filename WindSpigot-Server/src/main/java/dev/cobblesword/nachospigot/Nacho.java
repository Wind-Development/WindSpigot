package dev.cobblesword.nachospigot;

import java.util.Set;

import ga.windpvp.windspigot.WindSpigot;
import ga.windpvp.windspigot.protocol.MovementListener;
import ga.windpvp.windspigot.protocol.PacketListener;

@Deprecated
public class Nacho {

	private static Nacho INSTANCE;

	public Nacho() {
		INSTANCE = this;
	}

	public static Nacho get() {
		return INSTANCE == null ? new Nacho() : INSTANCE;
	}

	public void registerCommands() {

	}

	public void registerPacketListener(PacketListener packetListener) {
		WindSpigot.getInstance().registerPacketListener(packetListener);
	}

	public void unregisterPacketListener(PacketListener packetListener) {
		WindSpigot.getInstance().unregisterPacketListener(packetListener);
	}

	public Set<PacketListener> getPacketListeners() {
		return WindSpigot.getInstance().getPacketListeners();
	}

	public void registerMovementListener(MovementListener movementListener) {
		WindSpigot.getInstance().registerMovementListener(movementListener);
	}

	public void unregisterMovementListener(MovementListener movementListener) {
		WindSpigot.getInstance().unregisterMovementListener(movementListener);
	}

	public Set<MovementListener> getMovementListeners() {
		return WindSpigot.getInstance().getMovementListeners();
	}

}

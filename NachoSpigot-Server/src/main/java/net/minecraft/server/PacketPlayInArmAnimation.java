package net.minecraft.server;

import java.io.IOException;

public class PacketPlayInArmAnimation implements Packet<PacketListenerPlayIn> {

	public long timestamp; // Spigot

	public PacketPlayInArmAnimation() {
	}

	@Override
	public void a(PacketDataSerializer serializer) throws IOException {
		timestamp = System.currentTimeMillis(); // Spigot
	}

	@Override
	public void b(PacketDataSerializer serializer) throws IOException {
	}

	@Override
	public void a(PacketListenerPlayIn packetlistenerplayin) {
		packetlistenerplayin.a(this);
	}
}

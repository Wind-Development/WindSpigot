package net.minecraft.server;

import java.io.IOException;

public class PacketPlayInCloseWindow implements Packet<PacketListenerPlayIn> {

	private int id;

	public PacketPlayInCloseWindow() {
	}

	// CraftBukkit start
	public PacketPlayInCloseWindow(int id) {
		this.id = id;
	}
	// CraftBukkit end

	public int getId() {
		return id;
	}

	@Override
	public void a(PacketListenerPlayIn packetlistenerplayin) {
		packetlistenerplayin.a(this);
	}

	@Override
	public void a(PacketDataSerializer serializer) throws IOException {
		this.id = serializer.readByte();
	}

	@Override
	public void b(PacketDataSerializer serializer) throws IOException {
		serializer.writeByte(this.id);
	}
}

package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutOpenWindow implements Packet<PacketListenerPlayOut> {

	private int a;

	public int getId() {
		return this.a;
	}

	private String b;
	private IChatBaseComponent c;
	private int d;
	private int e;

	public PacketPlayOutOpenWindow() {
	}

	public PacketPlayOutOpenWindow(int i, String s, IChatBaseComponent ichatbasecomponent) {
		this(i, s, ichatbasecomponent, 0);
	}

	public PacketPlayOutOpenWindow(int i, String s, IChatBaseComponent ichatbasecomponent, int j) {
		this.a = i;
		this.b = s;
		this.c = ichatbasecomponent;
		this.d = j;
	}

	public PacketPlayOutOpenWindow(int i, String s, IChatBaseComponent ichatbasecomponent, int j, int k) {
		this(i, s, ichatbasecomponent, j);
		this.e = k;
	}

	public String getTitle() {
		return this.b;
	}

	public void setTitle(String title) {
		this.b = title;
	}

	@Override
	public void a(PacketListenerPlayOut packetlistenerplayout) {
		packetlistenerplayout.a(this);
	}

	@Override
	public void a(PacketDataSerializer serializer) throws IOException {
		this.a = serializer.readUnsignedByte();
		this.b = serializer.c(32);
		this.c = serializer.d();
		this.d = serializer.readUnsignedByte();
		if ("EntityHorse".equals(this.b)) {
			this.e = serializer.readInt();
		}

	}

	@Override
	public void b(PacketDataSerializer serializer) throws IOException {
		serializer.writeByte(this.a);
		serializer.a(this.b);
		serializer.a(this.c);
		serializer.writeByte(this.d);
		if ("EntityHorse".equals(this.b)) {
			serializer.writeInt(this.e);
		}

	}
}

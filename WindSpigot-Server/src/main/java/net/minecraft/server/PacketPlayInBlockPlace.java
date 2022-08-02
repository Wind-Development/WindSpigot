package net.minecraft.server;

import java.io.IOException;

import ga.windpvp.windspigot.config.WindSpigotConfig;
import io.netty.handler.codec.DecoderException;

public class PacketPlayInBlockPlace implements Packet<PacketListenerPlayIn> {

	private static final BlockPosition a = new BlockPosition(-1, -1, -1);
	private BlockPosition b;
	private int c;
	private ItemStack d;
	private float e;
	private float f;
	private float g;

	public long timestamp; // CraftBukkit

	public PacketPlayInBlockPlace() {
	}

	public PacketPlayInBlockPlace(ItemStack itemstack) {
		this(PacketPlayInBlockPlace.a, 255, itemstack, 0.0F, 0.0F, 0.0F);
	}

	public PacketPlayInBlockPlace(BlockPosition blockposition, int i, ItemStack itemstack, float f, float f1,
			float f2) {
		this.b = blockposition;
		this.c = i;
		this.d = itemstack != null ? itemstack.cloneItemStack() : null;
		this.e = f;
		this.f = f1;
		this.g = f2;
	}

	@Override
	public void a(PacketDataSerializer serializer) throws IOException {
		timestamp = System.currentTimeMillis(); // CraftBukkit
		this.b = serializer.c();
		this.c = serializer.readUnsignedByte();

		// KigPaper-0172 start - don't parse itemstack

		if (!WindSpigotConfig.stopDecodingItemStackOnPlace) {
			this.d = serializer.decodeItemStack();
		} else {
			// Consume everything and leave 3 bytes at the end
			if (serializer.readableBytes() < 3) {
				throw new DecoderException("Expected 3 facing bytes");
			}
			serializer.skipBytes(serializer.readableBytes() - 3);
		}

		// KigPaper-0172 end

		this.e = serializer.readUnsignedByte() / 16.0F;
		this.f = serializer.readUnsignedByte() / 16.0F;
		this.g = serializer.readUnsignedByte() / 16.0F;
	}

	@Override
	public void b(PacketDataSerializer serializer) throws IOException {
		serializer.a(this.b);
		serializer.writeByte(this.c);
		serializer.a(this.d);
		serializer.writeByte((int) (this.e * 16.0F));
		serializer.writeByte((int) (this.f * 16.0F));
		serializer.writeByte((int) (this.g * 16.0F));
	}

	@Override
	public void a(PacketListenerPlayIn packetlistenerplayin) {
		packetlistenerplayin.a(this);
	}

	public BlockPosition a() {
		return this.b;
	}

	public int getFace() {
		return this.c;
	}

	public ItemStack getItemStack() {
		return this.d;
	}

	public float d() {
		return this.e;
	}

	public float e() {
		return this.f;
	}

	public float f() {
		return this.g;
	}
}

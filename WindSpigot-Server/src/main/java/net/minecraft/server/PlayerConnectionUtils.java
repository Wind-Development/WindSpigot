package net.minecraft.server;

public class PlayerConnectionUtils {
    // PandaSpigot start - Redirect to MinecraftServer so that IAsyncTaskHandler can handle it
	public static <T extends PacketListener> void ensureMainThread(final Packet<T> packet, final T listener, WorldServer worldServer) throws CancelledPacketHandleException {
        ensureMainThread(packet, listener, worldServer.getMinecraftServer());
    }

    public static <T extends PacketListener> void ensureMainThread(final Packet<T> packet, final T listener, IAsyncTaskHandler<?> handler) throws CancelledPacketHandleException {
    // PandaSpigot end
		if (!handler.isMainThread()) {
			handler.postToMainThread(() -> packet.a(listener));
			throw CancelledPacketHandleException.INSTANCE;
		}
	}
}

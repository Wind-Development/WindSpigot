package net.minecraft.server;

public class PacketStatusListener implements PacketStatusInListener {

	private static final IChatBaseComponent a = new ChatComponentText("Status request has been handled.");
	private final MinecraftServer minecraftServer;
	private final NetworkManager networkManager;
	private boolean d;

	public PacketStatusListener(MinecraftServer minecraftserver, NetworkManager networkmanager) {
		this.minecraftServer = minecraftserver;
		this.networkManager = networkmanager;
	}

	@Override
	public void a(IChatBaseComponent ichatbasecomponent) {
	}

	@Override
	public void a(PacketStatusInStart packetstatusinstart) {
		if (this.d) {
			this.networkManager.close(PacketStatusListener.a);
			return;
		}
		this.d = true;
		// PandaSpigot start - Backport PaperServerListPingEvent
		com.destroystokyo.paper.network.StandardPaperServerListPingEventImpl.processRequest(this.minecraftServer,
				this.networkManager);
		// PandaSpigot end
	}

	@Override
	public void a(PacketStatusInPing packetstatusinping) {
		this.networkManager.handle(new PacketStatusOutPong(packetstatusinping.a()));
		this.networkManager.close(PacketStatusListener.a);
	}
}

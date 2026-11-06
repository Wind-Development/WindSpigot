package com.destroystokyo.paper.network;

import org.bukkit.entity.Player;
import org.bukkit.util.CachedServerIcon;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.MinecraftServer;

class PaperServerListPingEventImpl extends PaperServerListPingEvent {

	private final MinecraftServer server;

	PaperServerListPingEventImpl(MinecraftServer server, StatusClient client, int protocolVersion,
			CachedServerIcon icon) {
		// PandaSpigot - Backport PaperServerListPingEvent
		super(client, server.getMotd(), server.getPlayerList().getPlayerCount(),
				server.getPlayerList().getMaxPlayers(),
				server.getServerModName() + ' ' + server.getVersion(), protocolVersion, icon);
		this.server = server;
	}

	@Override
	protected final Object[] getOnlinePlayers() {
		return this.server.getPlayerList().players.toArray();
	}

	@Override
	protected final Player getBukkitPlayer(Object player) {
		return ((EntityPlayer) player).getBukkitEntity();
	}
}

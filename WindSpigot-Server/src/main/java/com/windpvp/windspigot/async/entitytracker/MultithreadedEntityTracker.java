package com.windpvp.windspigot.async.entitytracker;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldServer;

public class MultithreadedEntityTracker extends AsyncEntityTracker {
	
	public MultithreadedEntityTracker(WorldServer worldserver) {
		super(worldserver);
	}
	
	@Override
	public void updatePlayers() {
		super.updatePlayers();
		try {
            worldServer.ticker.getLatch().waitTillZero();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	    worldServer.ticker.getLatch().reset();
        for (EntityPlayer player : MinecraftServer.getServer().getPlayerList().players) {
            player.playerConnection.sendQueuedPackets();
        }
	}
}

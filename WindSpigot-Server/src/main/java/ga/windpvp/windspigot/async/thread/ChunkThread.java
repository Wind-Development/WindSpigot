package ga.windpvp.windspigot.async.thread;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import ga.windpvp.windspigot.async.netty.Spigot404Write;
import ga.windpvp.windspigot.config.WindSpigotConfig;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import net.minecraft.server.NetworkManager;
import net.minecraft.server.Packet;

public class ChunkThread extends AsyncPacketThread {

	private Map<NetworkManager, Queue<Packet<?>>> playerChunkPackets = new ConcurrentHashMap<>();
	private Object2IntArrayMap<NetworkManager> playerChunkPacketSends = new Object2IntArrayMap<>();
	
	public ChunkThread(String s) {
		super(s);
		tickTime = 1000000000 / WindSpigotConfig.chunkThreadTps;
	}

	// Handle chunk packets
	@Override
	public void run() {
		// Per player
		for (NetworkManager manager : playerChunkPackets.keySet()) {
			
			// Register the default packet sends
			if ((Integer) playerChunkPacketSends.getInt(manager) != null) {
				playerChunkPacketSends.put(manager, 0);
			}
			
			// Only do this when there are players that need chunk packets
			while (playerChunkPackets.get(manager).size() != 0) {
				
				// Stop sending after 5 packets
				if (playerChunkPacketSends.getInt(manager) == WindSpigotConfig.maxChunkSends) {
					playerChunkPacketSends.put(manager, 0);
					return;
				}
				// Send the chunk packet
                Spigot404Write.writeThenFlush(manager.channel, playerChunkPackets.get(manager).poll(), null);
                
                // Register the chunk send count
                playerChunkPacketSends.put(manager, playerChunkPacketSends.getInt(manager) + 1);
			}
			
		}
	}
	
    // Queue a packet
	@Override
    public void addPacket(final Packet<?>  packet, final NetworkManager manager, final GenericFutureListener<? extends Future<? super Void>>[] agenericfuturelistener) {
    	if (this.playerChunkPackets.get(manager) != null) {
    		this.playerChunkPackets.get(manager).add(packet);
    	} else {
    		Queue<Packet<?>> queue = new ConcurrentLinkedQueue<Packet<?>>();
    		queue.add(packet);
        	this.playerChunkPackets.put(manager, queue);
    	}
    }
	
	public void removePlayer(NetworkManager manager) {
		this.playerChunkPackets.remove(manager);
		this.playerChunkPacketSends.remove(manager);
	}
}

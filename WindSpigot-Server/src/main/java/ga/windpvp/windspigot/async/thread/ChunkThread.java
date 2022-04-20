package ga.windpvp.windspigot.async.thread;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.google.common.collect.Maps;

import ga.windpvp.windspigot.async.netty.Spigot404Write;
import ga.windpvp.windspigot.config.WindSpigotConfig;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import net.minecraft.server.NetworkManager;
import net.minecraft.server.Packet;

public class ChunkThread extends AsyncPacketThread {

	// These do not need to be concurrent collections due to synchronization
	private Map<NetworkManager, Queue<Packet<?>>> playerChunkPackets = Maps.newHashMap();
	private Object2IntArrayMap<NetworkManager> playerChunkPacketSends = new Object2IntArrayMap<>();
	
	public ChunkThread(String s) {
		super(s);
		tickTime = 1000000000 / WindSpigotConfig.chunkThreadTps;
	}

	// Handle chunk packets
	@Override
	public void run() {
		// Synchronize
		synchronized (playerChunkPackets) {
			synchronized (playerChunkPacketSends) {

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
						manager.dispatchPacket(playerChunkPackets.get(manager).poll(), null, true);

						// Register the chunk send count
						playerChunkPacketSends.put(manager, playerChunkPacketSends.getInt(manager) + 1);
					}
				}
				
			}
		}
	}
	
	// Queue a packet
	@Override
	public void addPacket(final Packet<?> packet, final NetworkManager manager,
			final GenericFutureListener<? extends Future<? super Void>>[] agenericfuturelistener) {
		// Synchronize
		synchronized (this.playerChunkPackets) {
			synchronized (this.playerChunkPacketSends) {
				
				// Add packet to queue if it already exists
				if (this.playerChunkPackets.get(manager) != null) {
					this.playerChunkPackets.get(manager).add(packet);
				} else {
					// Create a queue, put the packet, and map the queue
					Queue<Packet<?>> queue = new LinkedList<Packet<?>>(); // No need for thread safe collection because we synchronize
					queue.add(packet);
					this.playerChunkPackets.put(manager, queue);
				}
				
			}
		}
	}
	
	public void removePlayer(NetworkManager manager) {
		// Synchronize
		synchronized (this.playerChunkPackets) {
			synchronized (this.playerChunkPacketSends) {
				
				// Clear caches of packets and player packet sends
				this.playerChunkPackets.remove(manager);
				this.playerChunkPacketSends.remove(manager);
			}
			
		}
	}
}

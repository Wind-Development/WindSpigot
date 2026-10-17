package com.falchus.spigot.optimizations;

import com.windpvp.windspigot.config.WindSpigotConfig;
import com.google.common.collect.Queues;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import net.minecraft.server.NetworkManager;
import net.minecraft.server.Packet;

import java.util.ArrayList;
import java.util.Queue;

public class FastNetworkManager {

	private final NetworkManager networkManager;
	private final Queue<Packet<?>>[] queues = new Queue[WindSpigotConfig.trackingThreads];

	public FastNetworkManager(NetworkManager networkManager) {
		this.networkManager = networkManager;
		for (int i = 0; i < queues.length; i++) {
			queues[i] = Queues.newConcurrentLinkedQueue();
		}
	}

	public void writePacketLazily(Packet<?> packet, boolean flush) {
		Channel channel = networkManager.channel;
		if (channel == null || !channel.isActive()) return;

		channel.eventLoop().execute(() -> {
			ChannelFuture future = channel.write(packet);
			future.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
			if (flush) {
				channel.flush();
			}
		});
	}

	public void writePacketLazily(Packet<?> packet) {
		writePacketLazily(packet, false);
	}

	public void queuePacket(Packet<?> packet, int trackerThread) {
		if (packet == null) return;
		// trackerThread normally comes from the entity tracker and is in
		// [0, trackingThreads). Guard the index anyway: the queue array is sized once
		// when the connection is created, so a mismatch (e.g. trackingThreads changed,
		// or an unexpected caller) must not crash the netty/tracker thread with an
		// ArrayIndexOutOfBoundsException. Fall back to a direct lazy write instead.
		if (trackerThread < 0 || trackerThread >= queues.length) {
			writePacketLazily(packet);
			return;
		}
		queues[trackerThread].add(packet);
	}

	public void flushQueuedPackets() {
		Channel channel = networkManager.channel;
		if (channel == null || !channel.isActive()) return;

		ArrayList<Packet<?>> writing = new ArrayList<>();
		Packet<?> packet;
		for (int i = 0; i < queues.length; i++) {
			Queue<Packet<?>> current = queues[i];
			queues[i] = Queues.newConcurrentLinkedQueue();
			while ((packet = current.poll()) != null) {
				writing.add(packet);
			}
		}
		if (writing.isEmpty()) return;

		channel.eventLoop().execute(() -> {
			for (Packet<?> p : writing) {
				ChannelFuture future = channel.write(p);
				future.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
			}
			channel.flush();
		});
	}
}

// From
// https://github.com/Argarian-Network/NachoSpigot/tree/async-kb-hit
package com.windpvp.windspigot.async.netty;

import com.google.common.collect.Queues;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.Queue;
import net.minecraft.server.Packet;

public class Spigot404Write {
    // WindSpigot - GamingOP69 - was static (shared across all players — caused cross-player packet mixing).
    // Must be per-instance so each channel only drains its own packets.
    private final Queue<PacketQueue> packetsQueue = Queues.newConcurrentLinkedQueue();
    private final Tasks tasks = new Tasks();
    private Channel channel;

    public Spigot404Write(Channel channel) {
        this.channel = channel;
    }

    public static void writeThenFlush(Channel channel, Packet<?> value, GenericFutureListener<? extends Future<? super Void>>[] listener) {
        try {
            Spigot404Write writer = new Spigot404Write(channel);
            writer.packetsQueue.add(new PacketQueue(value, listener));
            if (writer.tasks.addTask()) {
                channel.pipeline().lastContext().executor().execute(writer::writeQueueAndFlush);
            }
        } catch (NullPointerException ignored) {
            // The player might leave right before the packet is sent
        }
    }

    public void writeQueueAndFlush() {
        while (tasks.fetchTask()) {
            PacketQueue messages;
            while ((messages = packetsQueue.poll()) != null) {
                ChannelFuture future = this.channel.write(messages.getPacket());
                if (messages.getListener() != null) {
                    future.addListeners(messages.getListener());
                }

                future.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            }
        }
        this.channel.flush();
    }


    private static class PacketQueue {
        private Packet<?> item;
        private GenericFutureListener<? extends Future<? super Void>>[] listener;

        private PacketQueue(Packet<?> item, GenericFutureListener<? extends Future<? super Void>>[] listener) {
            this.item = item;
            this.listener = listener;
        }

        public Packet<?> getPacket() {
            return this.item;
        }

        public GenericFutureListener<? extends Future<? super Void>>[] getListener() {
            return this.listener;
        }
    }
} 
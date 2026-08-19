// From
// https://github.com/Argarian-Network/NachoSpigot/tree/async-kb-hit
package com.windpvp.windspigot.async.thread;

public class CombatThread extends AsyncPacketThread {
    public CombatThread(String s) {
        super(s);
    }

    // Handle packets
    @Override
    public void run() {
        Runnable task;
        while ((task = this.packets.poll()) != null) {
            task.run();
        }
    }
} 

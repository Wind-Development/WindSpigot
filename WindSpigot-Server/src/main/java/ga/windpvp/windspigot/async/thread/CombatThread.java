// From
// https://github.com/Argarian-Network/NachoSpigot/tree/async-kb-hit
package ga.windpvp.windspigot.async.thread;

import ga.windpvp.windspigot.config.WindSpigotConfig;

public class CombatThread extends AsyncPacketThread {
    public CombatThread(String s) {
        super(s);
       tickTime = 1000000000 / WindSpigotConfig.combatThreadTPS;
    }

    // Handle packets
    @Override
    public void run() {
        while (this.packets.size() > 0) {
            this.packets.poll().run();
        }
    }
} 

// From
// https://github.com/Argarian-Network/NachoSpigot/tree/async-kb-hit
package ga.windpvp.windspigot.async.thread;

public class CombatThread extends AsyncThread {
    public CombatThread(String s) {
        super(s);
    }

    @Override
    public void run() {
        while (this.packets.size() > 0) {
            ((Runnable)this.packets.poll()).run();
        }
    }
} 

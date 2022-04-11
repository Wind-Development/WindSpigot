package ga.windpvp.windspigot.async.thread;

import ga.windpvp.windspigot.async.AsyncThread;

public class Knockback extends AsyncThread {
    public Knockback(String s) {
        super(s);
    }

    @Override
    public void run() {
        while (this.packets.size() > 0) {
            ((Runnable)this.packets.poll()).run();
        }
    }
} 

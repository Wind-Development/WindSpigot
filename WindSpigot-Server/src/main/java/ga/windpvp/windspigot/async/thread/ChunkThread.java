package ga.windpvp.windspigot.async.thread;

import ga.windpvp.windspigot.config.WindSpigotConfig;

public class ChunkThread extends AsyncPacketThread {
	
	public ChunkThread(String s) {
		super(s);
	    tickTime = 1000000000 / WindSpigotConfig.chunkThreadTps;
	}

    // Handle chunk packets
    @Override
    public void run() {
    	// Loop through the packets
        while (this.packets.size() > 0) {
            this.packets.poll().run();
        }
    }

}

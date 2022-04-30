package ga.windpvp.windspigot.async.pathsearch;

import ga.windpvp.windspigot.async.pathsearch.job.PathSearchJob;
import ga.windpvp.windspigot.async.pathsearch.thread.PathSearchThrottlerThread;

//This is based on Minetick's async path searching
public class AsyncPathSearchManager {
	
	private static AsyncPathSearchManager INSTANCE;
	private PathSearchThrottlerThread pathSearchThrottler;

	public AsyncPathSearchManager(int poolSize) {
		INSTANCE = this;
		this.pathSearchThrottler = new PathSearchThrottlerThread(poolSize);
	}

	public static boolean queuePathSearch(PathSearchJob pathSearchJob) {
		return INSTANCE.pathSearchThrottler.queuePathSearch(pathSearchJob);
	}
}

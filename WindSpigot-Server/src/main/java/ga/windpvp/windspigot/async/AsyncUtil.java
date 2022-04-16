package ga.windpvp.windspigot.async;

import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

public class AsyncUtil {

	public static void run(Runnable runnable) {
		ForkJoinPool.commonPool().execute(runnable);
	}
	
	public static void run(Runnable runnable, Executor executor) {
		executor.execute(runnable);
	}

}

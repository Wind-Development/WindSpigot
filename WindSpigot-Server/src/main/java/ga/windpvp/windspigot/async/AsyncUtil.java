package ga.windpvp.windspigot.async;

import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

public class AsyncUtil {

	/**
	 * Runs a given task async
	 * @param runnable The task to run
	 */
	public static void run(Runnable runnable) {
		ForkJoinPool.commonPool().execute(runnable);
	}
	
	/**
	 * Runs a given task on a specified Executor
	 * @param runnable The task to run
	 * @param executor The executor to run this task on
	 */
	public static void run(Runnable runnable, Executor executor) {
		executor.execute(runnable);
	}

}

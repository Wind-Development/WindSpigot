package ga.windpvp.windspigot.async;

import net.minecraft.server.MinecraftServer;

import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Supplier;

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
	
	/**
	 * Runs a given task the next tick on the main thread
	 * @param runnable The task to run
	 */
	public static void runSyncNextTick(Runnable runnable) {
		MinecraftServer.getServer().processQueue.add(runnable);
	}
	
	/**
	 * Runs a given task after the current tick on the main thread
	 * @param runnable The task to run
	 */
	public static void runPostTick(Runnable runnable) {
		MinecraftServer.getServer().priorityProcessQueue.add(runnable);
	}

	/**
	 * Runs a given task if it is synchronized on an object
	 * @param monitor The object to check for locking
	 * @param runnable The task to run
	 */
	@SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
	public static void runSynchronized(Object monitor, Runnable runnable) {
		if (Thread.holdsLock(monitor) ) {
			runnable.run();
		} else {
			synchronized (monitor) {
				runnable.run();
			}
		}
	}

	/**
	 * Runs a given task if it is synchronized on an object
	 * @param monitor The object to check for locking
	 * @param supplier The task to run
	 */
	@SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
	public static <T> T runSynchronized(Object monitor, Supplier<T> supplier) {
		if (Thread.holdsLock(monitor) ) {
			return supplier.get();
		} else {
			synchronized (monitor) {
				return supplier.get();
			}
		}
	}
}

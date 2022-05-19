package xyz.sculas.nacho.async;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class AsyncExplosions {
	public static ThreadPoolExecutor EXECUTOR;

	public static void initExecutor(int size) {
		EXECUTOR = (ThreadPoolExecutor) Executors.newFixedThreadPool(size);
	}

	public static void stopExecutor() {
		if (EXECUTOR != null) {
			EXECUTOR.shutdown();
		}
	}
}
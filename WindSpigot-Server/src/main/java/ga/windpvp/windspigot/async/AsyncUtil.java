package ga.windpvp.windspigot.async;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class AsyncUtil {

	private static Executor executor = Executors
			.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("WindSpigot Async Executor Thread").build());

	public static void run(Runnable runnable) {
		executor.execute(runnable);
	}

}

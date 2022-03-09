package ga.windpvp.windspigot.async;

import java.util.concurrent.CompletableFuture;

public class AsyncUtil {

	public static void run(Runnable runnable) {
		CompletableFuture.runAsync(runnable);
	}

}

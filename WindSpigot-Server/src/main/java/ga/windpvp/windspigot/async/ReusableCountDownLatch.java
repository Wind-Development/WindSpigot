package ga.windpvp.windspigot.async;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

// Basically a count down latch that can be reused, this means less GC
public class ReusableCountDownLatch {

	private final AtomicInteger threadsToCountDown;

	private final int threads;
	private final AtomicBoolean canBeReset;

	public ReusableCountDownLatch(int threads) {
		if (threads <= 0) {
			throw new IllegalArgumentException("Threads must be greater than 0.");
		}
		
		this.threads = threads;
		
		this.threadsToCountDown = new AtomicInteger(threads);
		this.canBeReset = new AtomicBoolean(false);
	}

	public void reset() {
		if (this.canBeReset.get()) {
			this.threadsToCountDown.set(threads);
			this.canBeReset.set(false);
		} else {
			throw new UnsupportedOperationException("Latch cannot be reset while not finished counting down.");
		}
	}

	public void countDown() {
		if (this.threadsToCountDown.decrementAndGet() == 0) {
			this.canBeReset.set(true);
		}
	}

	public void await() throws InterruptedException {
		do {
			Thread.sleep(1);
		} while (threadsToCountDown.get() != 0);

	}

}

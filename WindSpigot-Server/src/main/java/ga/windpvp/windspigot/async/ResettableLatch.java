package ga.windpvp.windspigot.async;

import javafixes.concurrency.ReusableCountLatch;

public class ResettableLatch extends ReusableCountLatch {

	private int initValue;

	public ResettableLatch() {
		this(0);
	}

	public ResettableLatch(int initialCount) {
		super(initialCount);
		this.initValue = initialCount;
	}

	/*
	 * Resets this latch to its value when constructed
	 */
	public void reset() {
		reset(initValue);
	}
	
	/*
	 * Resets this latch to a value
	 */
	public void reset(int count) {
		
		if (getCount() > count) {

			while (getCount() > count) {
				// Decrease the thread count of the latch if it is too high
				decrement();
			}

		} else if (getCount() < count) {

			while (getCount() < count) {
				// Increase the thread count of the latch if it is too low
				increment();
			}

		}
		
	}

}

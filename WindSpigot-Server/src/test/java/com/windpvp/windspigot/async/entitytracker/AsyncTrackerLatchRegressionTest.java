package com.windpvp.windspigot.async.entitytracker;

import com.windpvp.windspigot.async.ResettableLatch;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class AsyncTrackerLatchRegressionTest {

    @Test
    public void testLatchDecrementsEvenWhenTaskThrowsException() throws InterruptedException {
        int threads = 4;
        ResettableLatch latch = new ResettableLatch(threads);
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        AtomicInteger handledErrors = new AtomicInteger(0);

        for (int i = 0; i < threads; i++) {
            final int taskId = i;
            executor.submit(() -> {
                try {
                    if (taskId % 2 == 0) {
                        // Simulate unexpected exception during entity tracking
                        throw new RuntimeException("Simulated entity update error in thread " + taskId);
                    }
                } catch (Throwable t) {
                    handledErrors.incrementAndGet();
                } finally {
                    // Critical fix: latch decrement is in finally block
                    latch.decrement();
                }
            });
        }

        // Must not deadlock or hang
        latch.waitTillZero();
        executor.shutdown();

        Assert.assertEquals("Latch must count down to zero", 0, latch.getCount());
        Assert.assertEquals(2, handledErrors.get());
    }
}

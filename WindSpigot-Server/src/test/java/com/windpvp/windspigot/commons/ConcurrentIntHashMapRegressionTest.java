package com.windpvp.windspigot.commons;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrentIntHashMapRegressionTest {

    @Test
    public void testConcurrentAccess() throws InterruptedException {
        ConcurrentIntHashMap<String> map = new ConcurrentIntHashMap<>();
        int threadCount = 8;
        int operationsPerThread = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger errors = new AtomicInteger(0);

        for (int t = 0; t < threadCount; t++) {
            final int threadId = t;
            executor.submit(() -> {
                try {
                    for (int i = 0; i < operationsPerThread; i++) {
                        int key = threadId * operationsPerThread + i;
                        map.a(key, "value_" + key);
                        if (!map.b(key)) {
                            errors.incrementAndGet();
                        }
                        String val = map.get(key);
                        if (!("value_" + key).equals(val)) {
                            errors.incrementAndGet();
                        }
                    }
                } catch (Throwable t1) {
                    errors.incrementAndGet();
                    t1.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        Assert.assertEquals(0, errors.get());
        for (int t = 0; t < threadCount; t++) {
            for (int i = 0; i < operationsPerThread; i++) {
                int key = t * operationsPerThread + i;
                Assert.assertEquals("value_" + key, map.get(key));
            }
        }
    }
}

package com.windpvp.windspigot.async;

import net.minecraft.server.LazyInitVar;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class LazyInitVarTest {

    @Test
    public void testSupplierCalledExactlyOnceUnderHighContention() throws InterruptedException {
        AtomicInteger invocationCount = new AtomicInteger(0);
        LazyInitVar<String> lazyVar = new LazyInitVar<>(() -> {
            invocationCount.incrementAndGet();
            try {
                Thread.sleep(10); // simulate expensive init
            } catch (InterruptedException ignored) {}
            return "initialized_value";
        });

        Assert.assertFalse("Should not be initialized initially", lazyVar.isInitialized());

        int threadCount = 32;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    String val = lazyVar.get();
                    Assert.assertEquals("initialized_value", val);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        boolean finished = doneLatch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        Assert.assertTrue("All threads should have completed", finished);
        Assert.assertTrue("Should report initialized", lazyVar.isInitialized());
        Assert.assertEquals("Supplier must be executed exactly once", 1, invocationCount.get());
    }
}

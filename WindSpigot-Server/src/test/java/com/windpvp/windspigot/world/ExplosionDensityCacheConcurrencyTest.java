package com.windpvp.windspigot.world;

import it.unimi.dsi.fastutil.ints.Int2FloatMap;
import it.unimi.dsi.fastutil.ints.Int2FloatMaps;
import it.unimi.dsi.fastutil.ints.Int2FloatOpenHashMap;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ExplosionDensityCacheConcurrencyTest {

    @Test
    public void testConcurrentExplosionDensityCacheAccess() throws InterruptedException {
        Int2FloatMap cache = Int2FloatMaps.synchronize(new Int2FloatOpenHashMap());
        cache.defaultReturnValue(-1.0f);

        int writers = 8;
        int readers = 8;
        int total = writers + readers;
        ExecutorService executor = Executors.newFixedThreadPool(total);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(total);
        AtomicBoolean failed = new AtomicBoolean(false);

        for (int i = 0; i < writers; i++) {
            final int id = i;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    for (int j = 0; j < 1000; j++) {
                        int key = (id * 1000) + j;
                        cache.put(key, (float) (j * 0.1));
                        if (j % 200 == 0) {
                            cache.clear();
                        }
                    }
                } catch (Throwable t) {
                    failed.set(true);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        for (int i = 0; i < readers; i++) {
            final int id = i;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    for (int j = 0; j < 1000; j++) {
                        int key = (id * 1000) + j;
                        float val = cache.get(key);
                        // Read should not throw or corrupt
                    }
                } catch (Throwable t) {
                    failed.set(true);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        boolean completed = doneLatch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        Assert.assertTrue("Tasks should complete within timeout", completed);
        Assert.assertFalse("Concurrent operations on synchronized map must not throw exceptions", failed.get());
    }
}

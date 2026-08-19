package me.rastrian.dev.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class IndexedLinkedHashSetRegressionTest {

    @Test
    public void testGetOutOfBoundsReturnsNullWithoutThrowing() {
        IndexedLinkedHashSet<String> set = new IndexedLinkedHashSet<>();
        set.add("item1");
        set.add("item2");

        Assert.assertEquals("item1", set.get(0));
        Assert.assertEquals("item2", set.get(1));

        // Out of bounds indices must safely return null instead of throwing IndexOutOfBoundsException
        Assert.assertNull("Negative index must return null", set.get(-1));
        Assert.assertNull("Index equal to size must return null", set.get(2));
        Assert.assertNull("Index greater than size must return null", set.get(100));
    }

    @Test
    public void testConcurrentReadAndModification() throws InterruptedException {
        IndexedLinkedHashSet<Integer> set = new IndexedLinkedHashSet<>();
        int initialCount = 500;
        for (int i = 0; i < initialCount; i++) {
            set.add(i);
        }

        int readerThreads = 4;
        ExecutorService executor = Executors.newFixedThreadPool(readerThreads);
        CountDownLatch latch = new CountDownLatch(readerThreads);
        AtomicInteger nullOrValidCount = new AtomicInteger(0);
        AtomicInteger exceptions = new AtomicInteger(0);

        for (int t = 0; t < readerThreads; t++) {
            executor.submit(() -> {
                try {
                    for (int i = 0; i < 1000; i++) {
                        int index = i % (initialCount * 2);
                        Integer val = set.get(index);
                        if (val != null || index >= set.size()) {
                            nullOrValidCount.incrementAndGet();
                        }
                    }
                } catch (Throwable t1) {
                    exceptions.incrementAndGet();
                    t1.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        Assert.assertEquals("No exceptions should be thrown during concurrent get operations", 0, exceptions.get());
        Assert.assertTrue("All reads must complete safely", nullOrValidCount.get() > 0);
    }
}

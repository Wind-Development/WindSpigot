package com.windpvp.windspigot.server;

import org.junit.Assert;
import org.junit.Test;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ProcessQueueSafetyRegressionTest {

    @Test
    public void testFailingTaskDoesNotHaltQueueProcessing() {
        Queue<Runnable> queue = new ConcurrentLinkedQueue<>();
        AtomicInteger executedCount = new AtomicInteger(0);
        AtomicInteger caughtErrors = new AtomicInteger(0);

        // Task 1: Normal
        queue.add(() -> executedCount.incrementAndGet());
        // Task 2: Throws IllegalStateException (an Exception subclass — must be isolated)
        queue.add(() -> {
            throw new IllegalStateException("Task 2 failed purposefully");
        });
        // Task 3: Normal
        queue.add(() -> executedCount.incrementAndGet());
        // Task 4: Normal
        queue.add(() -> executedCount.incrementAndGet());

        // Process queue simulating MinecraftServer main loop fix (catch Exception, not Throwable)
        Runnable task;
        while ((task = queue.poll()) != null) {
            try {
                task.run();
            } catch (Exception e) {
                // Only Exception — Errors propagate
                caughtErrors.incrementAndGet();
            }
        }

        Assert.assertEquals("All 3 healthy tasks must have executed", 3, executedCount.get());
        Assert.assertEquals("1 failing task error caught and isolated", 1, caughtErrors.get());
        Assert.assertTrue("Queue must be empty after processing", queue.isEmpty());
    }

    @Test
    public void testEmptyQueuePollDoesNotThrowNoSuchElementException() {
        Queue<Runnable> queue = new ConcurrentLinkedQueue<>();

        // Polling an empty queue must safely return null
        Runnable task = queue.poll();
        Assert.assertNull("Polling empty queue returns null", task);
    }

    @Test
    public void testErrorsAreNotSwallowedByExceptionCatch() {
        // catch(Exception) must NOT catch Error subclasses.
        // Verify that a JVM Error propagates through the catch(Exception) boundary.
        Queue<Runnable> queue = new ConcurrentLinkedQueue<>();
        queue.add(() -> { throw new OutOfMemoryError("simulated OOM"); });

        boolean errorPropagated = false;
        Runnable task = queue.poll();
        if (task != null) {
            try {
                try {
                    task.run();
                } catch (Exception e) {
                    // Should NOT reach here
                    Assert.fail("OutOfMemoryError must not be caught by catch(Exception)");
                }
            } catch (OutOfMemoryError oom) {
                errorPropagated = true; // Correct — Error propagated past the Exception catch
            }
        }
        Assert.assertTrue("JVM Errors must propagate and not be swallowed by catch(Exception)", errorPropagated);
    }
}


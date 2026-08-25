package net.minecraft.server;

import com.google.common.collect.Queues;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Queue;
import java.util.concurrent.locks.LockSupport;
import java.util.function.BooleanSupplier;

public abstract class IAsyncTaskHandler<R extends Runnable> {

    public static final long BLOCK_TIME_NANOS = 100000L;
    private static final Logger LOGGER = LogManager.getLogger();
    private final String name;
    private final Queue<R> pendingTasks = Queues.newConcurrentLinkedQueue();
    private int blockingCount;

    protected IAsyncTaskHandler(final String name) {
        this.name = name;
    }

    public static boolean isNonRecoverable(final Throwable t) {
        return t instanceof ReportedException ? isNonRecoverable(t.getCause()) : t instanceof OutOfMemoryError || t instanceof StackOverflowError;
    }

    protected abstract R wrapRunnable(final Runnable runnable);

    protected abstract boolean shouldRun(final R task);

    public boolean isMainThread() {
        return Thread.currentThread() == this.aM();
    } // Poor name, just to maintain compatibility

    protected abstract Thread aM(); // Poor name, just to maintain compatibility

    protected boolean scheduleExecutables() {
        return !this.isMainThread();
    }

    public String name() {
        return this.name;
    }

    public void schedule(final R task) {
        this.pendingTasks.add(task);
        LockSupport.unpark(aM());
    }

    public void execute(final Runnable command) {
        R task = this.wrapRunnable(command);
        if (this.scheduleExecutables()) {
            this.schedule(task);
        } else {
            this.doRunTask(task);
        }
    }

    public void postToMainThread(final Runnable runnable) {
        execute(runnable);
    }

    protected void runAllTasks() {
        while (this.pollTask()) {
        }
    }

    protected boolean pollTask() {
        R task = this.pendingTasks.peek();
        if (task == null) {
            return false;
        } else if (this.blockingCount == 0 && !shouldRun(task)) {
            return false;
        } else {
            this.doRunTask(this.pendingTasks.remove());
            return true;
        }
    }

    public void managedBlock(final BooleanSupplier condition) {
        this.blockingCount++;

        try {
            while (!condition.getAsBoolean()) {
                if (!this.pollTask()) {
                    this.waitForTasks();
                }
            }
        } finally {
            this.blockingCount--;
        }
    }

    protected void waitForTasks() {
        Thread.yield();
        LockSupport.parkNanos("waiting for tasks", BLOCK_TIME_NANOS);
    }

    protected void doRunTask(final R task) {
        try {
            task.run();
        } catch (Exception e) {
            LOGGER.fatal("Error executing task on {}", this.name, e);
            if (isNonRecoverable(e)) {
                throw e;
            }
        }
    }
}

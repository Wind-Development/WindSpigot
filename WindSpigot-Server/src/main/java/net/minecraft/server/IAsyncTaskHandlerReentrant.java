package net.minecraft.server;

public abstract class IAsyncTaskHandlerReentrant<R extends Runnable> extends IAsyncTaskHandler<R> {

    private int reentrantCount;

    public IAsyncTaskHandlerReentrant(final String name) {
        super(name);
    }

    @Override
    protected boolean scheduleExecutables() {
        return this.runningTask() || super.scheduleExecutables();
    }

    protected boolean runningTask() {
        return this.reentrantCount > 0;
    }

    @Override
    protected void doRunTask(final R task) {
        this.reentrantCount++;

        try {
            super.doRunTask(task);
        } finally {
            this.reentrantCount--;
        }
    }
}

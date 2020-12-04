package org.liujk.java.framework.base.utils.thread.priority;


public class PriorityRunnableWrapper implements Runnable, Priorityable {

    private Priority priority;
    private Runnable runnable;

    public PriorityRunnableWrapper(Priority priority, Runnable runnable) {
        this.priority = priority;
        this.runnable = runnable;
    }

    @Override
    public Priority getPriority() {
        return this.priority;
    }

    @Override
    public void run() {
        this.runnable.run();
    }
}


package org.liujk.java.framework.base.utils.thread;



import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;

/**
 * 说明：
 * <p>
 * 尽量缩短任务等待时间，当parent.getPoolSize() < parent.getMaximumPoolSize()时，创建新线程执行任务，当线程有空闲时，不创建新线程
 *
 */
public class MonitoredThreadPoolQueue extends LinkedBlockingQueue<Runnable> {
    private volatile MonitoredThreadPoolExecutor parent = null;

    public MonitoredThreadPoolQueue (int capacity) {
        super (capacity);
    }

    public void setParent (MonitoredThreadPoolExecutor tp) {
        parent = tp;
    }

    @Override
    public boolean offer (Runnable o) {
        // do nothing
        if (parent == null) {
            return super.offer (o);
        }
        // 当线程池到达最大后，把任务放入queue
        if (parent.getPoolSize () == parent.getMaximumPoolSize ()) {
            return super.offer (o);
        }
        // 如果有空闲线程，把任务放入队列
        if (parent.getSubmittedCount () < (parent.getPoolSize ())) {
            return super.offer (o);
        }
        // 如果线程没有达到最大线程，创建新线程
        if (parent.getPoolSize () < parent.getMaximumPoolSize ()) {
            return false;
        }
        // 把任务加入队列
        return super.offer (o);
    }

    public boolean force (Runnable o) {
        if (parent == null || parent.isShutdown ()) {
            throw new RejectedExecutionException ("线程池已关闭，提交任务失败");
        }
        return super.offer (o);
    }
}

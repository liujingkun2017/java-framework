package org.liujk.java.framework.base.utils.thread.priority;


import org.liujk.java.framework.base.utils.thread.MonitoredThreadPool;
import org.liujk.java.framework.base.utils.thread.MonitoredThreadPoolExecutor;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;


public class PriorityableTheadPool extends MonitoredThreadPool {


    protected MonitoredThreadPoolExecutor createThreadPoolExecutor(int queueCapacity, ThreadFactory threadFactory,
                                                                   RejectedExecutionHandler rejectedExecutionHandler) {
        MonitoredThreadPoolExecutor executor;
        if (queueCapacity == 0) {
            executor = new MonitoredThreadPoolExecutor(getCorePoolSize(), getMaxPoolSize(),
                    getKeepAliveSeconds(), TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>(),
                    threadFactory, rejectedExecutionHandler);
        } else {
            PriorityableThreadPoolQueue queue = new PriorityableThreadPoolQueue(queueCapacity);
            executor = new MonitoredThreadPoolExecutor(getCorePoolSize(), getMaxPoolSize(),
                    getKeepAliveSeconds(), TimeUnit.SECONDS, queue, threadFactory,
                    rejectedExecutionHandler);
            queue.setParent(executor);
        }
        return executor;
    }
}

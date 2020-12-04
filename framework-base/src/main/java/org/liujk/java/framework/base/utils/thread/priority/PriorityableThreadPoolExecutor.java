package org.liujk.java.framework.base.utils.thread.priority;


import org.liujk.java.framework.base.utils.thread.MonitoredThreadPoolExecutor;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;


public class PriorityableThreadPoolExecutor extends MonitoredThreadPoolExecutor {

    public PriorityableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, new PriorityableThreadPoolQueue(Integer.MAX_VALUE));
    }

    public PriorityableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                          ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, new PriorityableThreadPoolQueue(Integer.MAX_VALUE),
                threadFactory);
    }

    public PriorityableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                          RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, new PriorityableThreadPoolQueue(Integer.MAX_VALUE),
                handler);
    }

    public PriorityableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                          ThreadFactory threadFactory,
                                          RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, new PriorityableThreadPoolQueue(Integer.MAX_VALUE),
                threadFactory, handler);
    }
}

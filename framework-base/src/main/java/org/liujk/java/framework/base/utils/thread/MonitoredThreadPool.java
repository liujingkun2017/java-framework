
package org.liujk.java.framework.base.utils.thread;



import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;

/**
 * 说明：
 * <p>
 * 基于Spring线程池的受监控的线程池，具有通过JMX监控线程池运行状况的功能。目前可监控的参数分两类：
 * <p>
 * 1、线程池相关的参数 activeThreadCnt：当前活跃线程数
 * poolSize：当前线程池大小 corePoolSize：当前核心线程池大小 maxPoolSize：设置的线程池最大上限 taskInQueue：队列中任务数
 * <p>
 * 2、任务执行状态
 * taskTimer：任务执行的耗时（任务开始执行到执行完毕的耗时，不包括入队列和在队列中等待的时间），包括执行的任务数目，任务执行频率， 平均耗时，最大耗时，最小耗时，耗时分布等。
 * enqTimer：队列等待耗时（从提交入队列申请到出队列准备执行），包括平均耗时，耗时分布
 * responseTimer：任务响应耗时（从提交入队列申请到执行完毕的耗时，理论上等于taskTimer + enqTimer），包括执行的任务数目，任务执行
 * 频率，平均耗时，最大耗时，最小耗时，耗时分布等。
 * <p>
 * 如果队列等待耗时较大，任务执行耗时较小或者出现任务提交被拒绝的情况，说明线程池处理能力达到上限，需要增大线程池或者增加硬件；如果等
 * 待耗时较小，但任务处理耗时较大，导致整体响应耗时较大，说明业务逻辑可能需要优化。
 * <p>
 * 可通过setMetrics方法设置监控选项，其中： name: 监控线程池的名称，默认使用类名作为前缀 enableTimerMetric: 是否开启任务执行状态监控，默认不开启
 * enableGaugeMetric：是否开启线程池相关参数监控，默认开启 rateUnit：频率显示单位，默认使用task/sec durationUnit：耗时显示单位，默认使用毫秒。
 * <p>
 * enableTimerMetric 和 enableGaugeMetric 这两个参数支持运行时动态调整。
 *
 */
public class MonitoredThreadPool extends ThreadPoolTaskExecutor {
    private static final int DEFAULT_AWAIT_TERMINATION_SECONDS = 60;

    private String metricName = "MonitoredThreadPool";
    private TimeUnit rateUnit = TimeUnit.SECONDS;
    private TimeUnit durationUnit = TimeUnit.MILLISECONDS;

    private volatile boolean enableTimerMetric = false;
    private volatile boolean enableGaugeMetric = true;

    private volatile int queueCapacity = 5000;
    private volatile int corePoolSize = 16;
    private volatile int maxPoolSize = 100;
    private volatile int keepAliveSeconds = 240;
    private volatile boolean allowCoreThreadTimeOut = false;

    private volatile MonitoredThreadPoolExecutor threadPoolExecutor;

    /**
     * 添加默认配置
     */
    public MonitoredThreadPool () {
        this.setAwaitTerminationSeconds (DEFAULT_AWAIT_TERMINATION_SECONDS);
        this.setWaitForTasksToCompleteOnShutdown (true);
        this.setThreadNamePrefix (metricName);
        this.setRejectedExecutionHandler (new ReporttingRejectedExecutionHandler (metricName, ReporttingRejectedExecutionHandler.Policy.callerRun));
        this.setDaemon (false);
        this.setAllowCoreThreadTimeOut (true);
    }

    public int getQueueCapacity () {
        return queueCapacity;
    }

    @Override
    public void setQueueCapacity (int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public boolean isAllowCoreThreadTimeOut () {
        return allowCoreThreadTimeOut;
    }

    @Override
    public void setAllowCoreThreadTimeOut (boolean allowCoreThreadTimeOut) {
        this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
        if (null != threadPoolExecutor) {
            threadPoolExecutor.allowCoreThreadTimeOut (allowCoreThreadTimeOut);
        }
    }

    @Override
    public int getCorePoolSize () {
        return this.corePoolSize;
    }

    @Override
    public void setCorePoolSize (int corePoolSize) {
        this.corePoolSize = corePoolSize;
        if (null != threadPoolExecutor) {
            threadPoolExecutor.setCorePoolSize (corePoolSize);
        }
    }

    @Override
    public int getMaxPoolSize () {
        return this.maxPoolSize;
    }

    @Override
    public void setMaxPoolSize (int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
        if (null != threadPoolExecutor) {
            threadPoolExecutor.setMaximumPoolSize (maxPoolSize);
        }
    }

    @Override
    public int getKeepAliveSeconds () {
        return this.keepAliveSeconds;
    }

    @Override
    public void setKeepAliveSeconds (int keepAliveSeconds) {
        this.keepAliveSeconds = keepAliveSeconds;
        if (null != threadPoolExecutor) {
            threadPoolExecutor.setKeepAliveTime (keepAliveSeconds, TimeUnit.SECONDS);
        }
    }

    @Override
    public int getActiveCount () {
        if (null == threadPoolExecutor) {
            throw new IllegalStateException ("MonitoredThreadPool hasn't been initialized!");
        }
        return threadPoolExecutor.getActiveCount ();
    }

    @Override
    public ThreadPoolExecutor getThreadPoolExecutor () throws IllegalStateException {
        if (null == threadPoolExecutor) {
            throw new IllegalStateException ("threadpool has not been initialized!");
        }
        return threadPoolExecutor;
    }

    @Override
    protected ExecutorService initializeExecutor (ThreadFactory threadFactory,
                                                  RejectedExecutionHandler rejectedExecutionHandler) {
        if (!(rejectedExecutionHandler instanceof ReporttingRejectedExecutionHandler)) {
            throw new IllegalArgumentException (
                    rejectedExecutionHandler + "必须为ReporttingRejectedExecutionHandler");
        }
        MonitoredThreadPoolExecutor executor;
        if (queueCapacity == 0) {
            executor = new MonitoredThreadPoolExecutor (getCorePoolSize (), getMaxPoolSize (),
                                                        getKeepAliveSeconds (), TimeUnit.SECONDS,
                                                        new SynchronousQueue<Runnable> (),
                                                        threadFactory, rejectedExecutionHandler);
        } else {
            MonitoredThreadPoolQueue queue = new MonitoredThreadPoolQueue (queueCapacity);
            executor = new MonitoredThreadPoolExecutor (getCorePoolSize (), getMaxPoolSize (),
                                                        getKeepAliveSeconds (), TimeUnit.SECONDS, queue, threadFactory,
                                                        rejectedExecutionHandler);
            queue.setParent (executor);
        }
        if (allowCoreThreadTimeOut) {
            executor.allowCoreThreadTimeOut (true);
        }
        executor.setMetrics (metricName, enableTimerMetric, enableGaugeMetric, rateUnit,
                             durationUnit);
        executor.initialize ();
        this.threadPoolExecutor = executor;
        return executor;
    }

    public void setMetrics (String name) {
        setMetrics (name, false, true);
    }

    @Override
    public void setThreadNamePrefix (String threadNamePrefix) {
        super.setThreadNamePrefix (threadNamePrefix);
        this.metricName = this.getThreadNamePrefix ();
        this.setRejectedExecutionHandler (new ReporttingRejectedExecutionHandler (metricName, ReporttingRejectedExecutionHandler.Policy.callerRun));
    }

    public void setMetrics (String name, boolean enableTimerMetric, boolean enableGaugeMetric) {
        setMetrics (name, enableTimerMetric, enableGaugeMetric, rateUnit, durationUnit);
    }

    public void setMetrics (String name, boolean enableTimerMetric, boolean enableGaugeMetric,
                            TimeUnit rateUnit, TimeUnit durationUnit) {
        this.metricName = name;
        this.enableTimerMetric = enableTimerMetric;
        this.enableGaugeMetric = enableGaugeMetric;
        this.rateUnit = rateUnit;
        this.durationUnit = durationUnit;
        if (null != threadPoolExecutor) {
            threadPoolExecutor.setMetrics (name, enableTimerMetric, enableGaugeMetric, rateUnit,
                                           durationUnit);
        }
    }

    public boolean isEnableTimerMetric () {
        return this.enableTimerMetric;
    }

    public synchronized void setEnableTimerMetric (boolean enableTimerMetric) {
        this.enableTimerMetric = enableTimerMetric;
        if (null != threadPoolExecutor) {
            threadPoolExecutor.setEnableTimerMetric (enableTimerMetric);
        }
    }

    public boolean isEnableGaugeMetric () {
        return this.enableGaugeMetric;
    }

    public synchronized void setEnableGaugeMetric (boolean enableGaugeMetric) {
        this.enableGaugeMetric = enableGaugeMetric;
        if (null != threadPoolExecutor) {
            threadPoolExecutor.setEnableGaugeMetric (enableGaugeMetric);
        }
    }

    /**
     * Returns a string identifying this pool, as well as its state,
     * including indications of run state and estimated worker and
     * task counts.
     *
     * @return a string identifying this pool, as well as its state
     */
    public String toString() {
        return  getClass ().getSimpleName ()+
                "[" +
                "ThreadName prefix = " + getPoolSize () +
                ", pool size = " + getPoolSize () +
                ", active threads = " + getActiveCount () +
                ", queue capacity = " + getQueueCapacity ()+
                "]";
    }

}

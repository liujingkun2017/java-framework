
package org.liujk.java.framework.base.utils.thread;


import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.base.Preconditions;
import org.liujk.java.framework.base.utils.lang.CollectionUtils;
import org.liujk.java.framework.base.utils.metrics.MetricsHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.liujk.java.framework.base.api.request.Base.SID_KEY;

/**
 * 说明：
 * <p>
 *
 */
public class MonitoredThreadPoolExecutor extends ThreadPoolExecutor {
    private final AtomicInteger submittedCount = new AtomicInteger (0);
    private Logger logger = LoggerFactory.getLogger (getClass ());
    private String metricName = "MonitoredThreadPoolExecutor";
    private MetricRegistry metricRegistry = MetricsHolder.metricRegistry ();
    private TimeUnit rateUnit = TimeUnit.SECONDS;
    private TimeUnit durationUnit = TimeUnit.MILLISECONDS;
    private Map<String, Gauge<?>> gaugeMap;
    private Timer taskTimer;
    private Timer enqTimer;
    private Timer responseTimer;
    private Map<String, Timer> timerMap;
    private volatile boolean enableTimerMetric = false;
    private volatile boolean enableGaugeMetric = true;
    private volatile boolean initialized = false;

    public MonitoredThreadPoolExecutor () {
        this (16, 100, 240, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable> (5000));
    }

    public MonitoredThreadPoolExecutor (int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                        TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        this (corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
              Executors.defaultThreadFactory ());
    }

    public MonitoredThreadPoolExecutor (int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                        TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        this (corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory,
              ReporttingRejectedExecutionHandler.DEFAULT);
    }

    public MonitoredThreadPoolExecutor (int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                        TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                        RejectedExecutionHandler handler) {
        this (corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
              Executors.defaultThreadFactory (), handler);
    }

    public MonitoredThreadPoolExecutor (int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                        TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
                                        RejectedExecutionHandler handler) {
        super (corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory,
               handler);
        if (this.getQueue () instanceof MonitoredThreadPoolQueue
                && (!(handler instanceof ReporttingRejectedExecutionHandler))) {
            throw new IllegalArgumentException (
                    "当使用MonitoredThreadPoolQueue时必须为ReporttingRejectedExecutionHandler");
        }
    }

    @Override
    public void execute (Runnable command) {
        if (!initialized) {
            initialize ();
        }

        submittedCount.incrementAndGet ();
        Preconditions.checkState (initialized);
        try {
            super.execute (wrapAll (command));
        } catch (RejectedExecutionException e) {
            submittedCount.decrementAndGet ();
            throw e;
        }
    }

    private Runnable wrapAll (Runnable command) {
        command = mdcGidWrap (command);
        if (enableTimerMetric) {
            command = newTimerRunnable (command);
        }
        return command;
    }

    private <T> Callable<T> wrapAllCallable (Callable<T> task) {
        task = mdcGidWrapCallable (task);
        if (enableTimerMetric) {
            task = newTimerCallable (task);
        }
        return task;
    }

    private Runnable mdcGidWrap (Runnable command) {
        String gid = MDC.get (SID_KEY);
        if (gid != null) {
            return new MDCGidRunnable (command, gid);
        } else {
            return command;
        }
    }

    private <T> Callable<T> mdcGidWrapCallable (Callable<T> task) {
        String gid = MDC.get (SID_KEY);
        if (gid != null) {
            return new MDCGidCallable (task, gid);
        } else {
            return task;
        }
    }

    @Override
    protected void beforeExecute (Thread t, Runnable r) {
        super.beforeExecute (t, r);
    }

    @Override
    protected void afterExecute (Runnable r, Throwable t) {
        submittedCount.decrementAndGet ();
        super.afterExecute (r, t);
    }

    public void setMetrics (String name) {
        setMetrics (name, false, true);
    }

    public void setMetrics (String name, boolean enableTimerMetric, boolean enableGaugeMetric) {
        this.metricName = name;
        this.enableTimerMetric = enableTimerMetric;
        this.enableGaugeMetric = enableGaugeMetric;
    }

    public void setMetrics (String name, boolean enableTimerMetric, boolean enableGaugeMetric,
                            TimeUnit rateUnit, TimeUnit durationUnit) {
        this.metricName = name;
        this.enableTimerMetric = enableTimerMetric;
        this.enableGaugeMetric = enableGaugeMetric;
        this.rateUnit = rateUnit;
        this.durationUnit = durationUnit;

    }

    public boolean isEnableTimerMetric () {
        return enableTimerMetric;
    }

    public synchronized void setEnableTimerMetric (boolean enableTimerMetric) {
        logger.info ("set enableTimerMetric to {}", enableTimerMetric);
        this.enableTimerMetric = enableTimerMetric;
        if (enableTimerMetric && null == timerMap) {
            initializeTimerMap ();
        }
        if (enableTimerMetric) {
            registerTimers ();
        } else {
            unregisterTimers ();
        }
    }

    public boolean isEnableGaugeMetric () {
        return enableGaugeMetric;
    }

    public synchronized void setEnableGaugeMetric (boolean enableGaugeMetric) {
        logger.info ("set enableGaugeMetric to {}", enableGaugeMetric);
        this.enableGaugeMetric = enableGaugeMetric;
        if (enableGaugeMetric && null == gaugeMap) {
            initializeGaugeMap ();
        }
        if (enableGaugeMetric) {
            registerGauges ();
        } else {
            unregisterGauges ();
        }
    }

    private void initializeGaugeMap () {
        if (CollectionUtils.isEmpty (gaugeMap)) {
            String taskGaugeName = MetricRegistry.name ("tp", "taskInQueue", metricName);
            Gauge<Integer> taskInQueue = new Gauge<Integer>() {
                @Override
                public Integer getValue () {
                    return getQueue ().size ();
                }
            };
            String submittedCountGaugeName = MetricRegistry.name ("tp", "submittedCount",
                                                                  metricName);
            Gauge<Integer> submittedCountGaugeNameCnt = new Gauge<Integer>() {
                @Override
                public Integer getValue () {
                    return getSubmittedCount ();
                }
            };
            if (null == gaugeMap) {
                gaugeMap = new HashMap<> ();
            }
            String remainingCapacitySize = MetricRegistry.name ("tp", "remainingCapacity",
                                                                metricName);
            Gauge<Integer> remainingCapacityCnt = new Gauge<Integer>() {
                @Override
                public Integer getValue () {
                    return getQueue ().remainingCapacity ();
                }
            };
            gaugeMap.put (taskGaugeName, taskInQueue);
            gaugeMap.put (submittedCountGaugeName, submittedCountGaugeNameCnt);
            gaugeMap.put (remainingCapacitySize, remainingCapacityCnt);

        }
    }

    private void initializeTimerMap () {
        if (CollectionUtils.isEmpty (timerMap)) {
            String taskTimerName = MetricRegistry.name ("tp", "task", metricName);
            taskTimer = new Timer();
            String enqTimerName = MetricRegistry.name ("tp", "enq", metricName);
            enqTimer = new Timer();
            String responsTimerName = MetricRegistry.name ("tp", "rep", metricName);
            responseTimer = new Timer();
            if (null == timerMap) {
                timerMap = new HashMap<> ();
            }
            timerMap.put (taskTimerName, taskTimer);
            timerMap.put (enqTimerName, enqTimer);
            timerMap.put (responsTimerName, responseTimer);
        }
    }

    private void registerGauges () {
        Preconditions.checkNotNull (gaugeMap);
        for (Map.Entry<String, Gauge<?>> gaugeEntry : gaugeMap.entrySet ()) {
            try {
                metricRegistry.register (gaugeEntry.getKey (), gaugeEntry.getValue ());
            } catch (IllegalArgumentException e) {
                // do nothing
            }
        }
    }

    private void unregisterGauges () {
        if (gaugeMap != null) {
            for (Map.Entry<String, Gauge<?>> gaugeEntry : gaugeMap.entrySet ()) {
                metricRegistry.remove (gaugeEntry.getKey ());
            }
        }
    }

    private void registerTimers () {
        if (timerMap != null) {
            for (Map.Entry<String, Timer> timerEntry : timerMap.entrySet ()) {
                try {
                    metricRegistry.register (timerEntry.getKey (), timerEntry.getValue ());
                } catch (IllegalArgumentException e) {
                    // do nothing
                }
            }
        }
    }

    private void unregisterTimers () {
        if (timerMap != null) {
            for (Map.Entry<String, Timer> timerEntry : timerMap.entrySet ()) {
                metricRegistry.remove (timerEntry.getKey ());
            }
        }
    }

    public void initialize () {
        synchronized (this) {
            if (!initialized) {
                Objects.requireNonNull (metricRegistry);
                initializeGaugeMap ();
                initializeTimerMap ();
                if (enableGaugeMetric) {
                    registerGauges ();
                }
                if (enableTimerMetric) {
                    registerTimers ();
                }
                // jmxReporter =
                // JmxReporter.forRegistry(metricRegistry).convertRatesTo(this.rateUnit)
                // .convertDurationsTo(this.durationUnit).build();
                // jmxReporter.start();
                initialized = true;
            }
        }
    }

    @Override
    public void shutdown () {
        super.shutdown ();
        synchronized (this) {
            if (initialized) {
                unregisterGauges ();
                unregisterTimers ();
            }
        }
    }

    /**
     * 获取提交到线程池没有处理完的任务数
     */
    public int getSubmittedCount () {
        return submittedCount.get ();
    }

    @Override
    public List<Runnable> shutdownNow () {
        final List<Runnable> runnables = super.shutdownNow ();
        synchronized (this) {
            unregisterGauges ();
            unregisterTimers ();
        }
        return runnables;
    }

    private TimerRunnable newTimerRunnable (Runnable task) {
        return new TimerRunnable (task, this.enqTimer.time (), this.responseTimer.time ());
    }

    private <T> TimerCallable<T> newTimerCallable (Callable<T> task) {
        return new TimerCallable (task, this.enqTimer.time (), this.responseTimer.time ());
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
                "pool size = " + getPoolSize () +
                ", active threads = " + getActiveCount () +
                ", submitted count = " + submittedCount.get () +
                ", queued tasks = " + getQueue ().size() +
                ", completed tasks = " + getCompletedTaskCount () +
                "]";
    }

    private static class MDCGidRunnable implements Runnable {
        private final Runnable task;
        private final String gid;

        public MDCGidRunnable (Runnable task, String gid) {
            this.task = task;
            this.gid = gid;
        }

        @Override
        public void run () {
            try {
                MDC.put (SID_KEY, gid);
                task.run ();
            } finally {
                MDC.remove (SID_KEY);
            }
        }
    }

    private static class MDCGidCallable<T> implements Callable<T> {
        private final Callable<T> task;
        private final String gid;

        public MDCGidCallable (Callable<T> task, String gid) {
            this.task = task;
            this.gid = gid;
        }

        @Override
        public T call () throws Exception {
            try {
                MDC.put (SID_KEY, gid);
                return task.call ();
            } finally {
                MDC.remove (SID_KEY);
            }
        }
    }

    private class TimerRunnable implements Runnable {
        private Runnable task;
        private Timer.Context enqContext;
        private Timer.Context totalContext;

        public TimerRunnable (Runnable task, Timer.Context enqContext, Timer.Context totalContext) {
            Preconditions.checkNotNull (task, "task shouldn't be null");
            Preconditions.checkNotNull (enqContext, "enqContext shouldn't be null");
            Preconditions.checkNotNull (totalContext, "totalContext shouldn't be null");
            this.task = task;
            this.enqContext = enqContext;
            this.totalContext = totalContext;
        }

        @Override
        public void run () {
            this.enqContext.stop ();
            Timer.Context taskContext = MonitoredThreadPoolExecutor.this.taskTimer.time ();
            try {
                task.run ();
            } finally {
                taskContext.stop ();
                this.totalContext.stop ();
            }
        }
    }

    private class TimerCallable<V> implements Callable<V> {
        private Callable<V> task;
        private Timer.Context enqContext;
        private Timer.Context totalContext;

        public TimerCallable (Callable<V> task, Timer.Context enqContext,
                              Timer.Context totalContext) {
            Preconditions.checkNotNull (task, "task shouldn't be null");
            Preconditions.checkNotNull (enqContext, "enqContext shouldn't be null");
            Preconditions.checkNotNull (totalContext, "totalContext shouldn't be null");
            this.task = task;
            this.enqContext = enqContext;
            this.totalContext = totalContext;
        }

        @Override
        public V call () throws Exception {
            this.enqContext.stop ();
            Timer.Context taskContext = MonitoredThreadPoolExecutor.this.taskTimer.time ();
            try {
                return task.call ();
            } finally {
                taskContext.stop ();
                this.totalContext.stop ();
            }
        }
    }
}
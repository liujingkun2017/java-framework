
package org.liujk.java.framework.base.utils.thread;



import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * 说明：
 * <p>
 * 实例化线程池
 *
 * @See java.util.concurrent.Executors
 */
public class MonitoredExecutors {


    /**
     * Cannot instantiate.
     */
    private MonitoredExecutors () {
    }

    /**
     * Creates a single-threaded executor that can schedule commands
     * to run after a given delay, or to execute periodically.
     * (Note however that if this single
     * thread terminates due to a failure during execution prior to
     * shutdown, a new one will take its place if needed to execute
     * subsequent tasks.)  Tasks are guaranteed to execute
     * sequentially, and no more than one task will be active at any
     * given time. Unlike the otherwise equivalent
     * {@code newScheduledThreadPool(1)} the returned executor is
     * guaranteed not to be reconfigurable to use additional threads.
     *
     * @return the newly created scheduled executor
     */
    public static ScheduledExecutorService newSingleThreadScheduledExecutor () {
        MonitoredScheduledThreadPoolExecutor monitoredScheduledThreadPoolExecutor
                = new MonitoredScheduledThreadPoolExecutor (1);
        monitoredScheduledThreadPoolExecutor.initialize ();

        return new DelegatedScheduledExecutorService (monitoredScheduledThreadPoolExecutor);
    }

    /**
     * Creates a single-threaded executor that can schedule commands
     * to run after a given delay, or to execute periodically.  (Note
     * however that if this single thread terminates due to a failure
     * during execution prior to shutdown, a new one will take its
     * place if needed to execute subsequent tasks.)  Tasks are
     * guaranteed to execute sequentially, and no more than one task
     * will be active at any given time. Unlike the otherwise
     * equivalent {@code newScheduledThreadPool(1, threadFactory)}
     * the returned executor is guaranteed not to be reconfigurable to
     * use additional threads.
     *
     * @param threadFactory the factory to use when creating new
     *                      threads
     *
     * @return a newly created scheduled executor
     * @throws NullPointerException if threadFactory is null
     */
    public static ScheduledExecutorService newSingleThreadScheduledExecutor (ThreadFactory threadFactory) {
        MonitoredScheduledThreadPoolExecutor monitoredScheduledThreadPoolExecutor
                = new MonitoredScheduledThreadPoolExecutor (1, threadFactory);
        monitoredScheduledThreadPoolExecutor.initialize ();

        return new DelegatedScheduledExecutorService (monitoredScheduledThreadPoolExecutor);
    }

    /**
     * Creates a thread pool that can schedule commands to run after a
     * given delay, or to execute periodically.
     *
     * @param corePoolSize the number of threads to keep in the pool,
     *                     even if they are idle
     *
     * @return a newly created scheduled thread pool
     * @throws IllegalArgumentException if {@code corePoolSize < 0}
     */


    public static ScheduledExecutorService newScheduledThreadPool (int corePoolSize) {
        return newScheduledThreadPool (corePoolSize, Executors.defaultThreadFactory (),
                                       ReporttingRejectedExecutionHandler.Policy.callerRun);
    }


    /**
     * Creates a thread pool that can schedule commands to run after a
     * given delay, or to execute periodically.
     *
     * @param corePoolSize  the number of threads to keep in the pool,
     *                      even if they are idle
     * @param threadFactory the factory to use when the executor
     *                      creates a new thread
     *
     * @return a newly created scheduled thread pool
     * @throws IllegalArgumentException if {@code corePoolSize < 0}
     * @throws NullPointerException     if threadFactory is null
     */
    public static ScheduledExecutorService newScheduledThreadPool (
            int corePoolSize, ThreadFactory threadFactory) {
        return newScheduledThreadPool (corePoolSize, threadFactory,
                                       ReporttingRejectedExecutionHandler.Policy.callerRun);
    }


    public static ScheduledExecutorService newScheduledThreadPool (int corePoolSize, ThreadFactory threadFactory,
                                                                   ReporttingRejectedExecutionHandler.Policy policy) {
        MonitoredScheduledThreadPoolExecutor monitoredScheduledThreadPoolExecutor
                = new MonitoredScheduledThreadPoolExecutor (corePoolSize, threadFactory,
                                                            new ReporttingRejectedExecutionHandler (policy));
        monitoredScheduledThreadPoolExecutor.initialize ();

        return monitoredScheduledThreadPoolExecutor;
    }

    /**
     * Creates a thread pool that reuses a fixed number of threads
     * operating off a shared unbounded queue, using the provided
     * ThreadFactory to create new threads when needed.  At any point,
     * at most {@code nThreads} threads will be active processing
     * tasks.  If additional tasks are submitted when all threads are
     * active, they will wait in the queue until a thread is
     * available.  If any thread terminates due to a failure during
     * execution prior to shutdown, a new one will take its place if
     * needed to execute subsequent tasks.  The threads in the pool will
     * exist until it is explicitly {@link ExecutorService#shutdown
     * shutdown}.
     *
     * @param nThreads      the number of threads in the pool
     * @param threadFactory the factory to use when creating new threads
     *
     * @return the newly created thread pool
     * @throws NullPointerException     if threadFactory is null
     * @throws IllegalArgumentException if {@code nThreads <= 0}
     */
    public static ThreadPoolExecutor newFixedThreadPool (int nThreads, ThreadFactory threadFactory) {
        MonitoredThreadPoolExecutor monitoredThreadPoolExecutor = new MonitoredThreadPoolExecutor (nThreads, nThreads,
                                                                                                   0L,
                                                                                                   TimeUnit.MILLISECONDS,
                                                                                                   new MonitoredThreadPoolQueue (
                                                                                                           Integer.MAX_VALUE),
                                                                                                   threadFactory);

        monitoredThreadPoolExecutor.initialize ();
        return monitoredThreadPoolExecutor;
    }

    /**
     * Creates an Executor that uses a single worker thread operating
     * off an unbounded queue. (Note however that if this single
     * thread terminates due to a failure during execution prior to
     * shutdown, a new one will take its place if needed to execute
     * subsequent tasks.)  Tasks are guaranteed to execute
     * sequentially, and no more than one task will be active at any
     * given time. Unlike the otherwise equivalent
     * {@code newFixedThreadPool(1)} the returned executor is
     * guaranteed not to be reconfigurable to use additional threads.
     *
     * @return the newly created single-threaded Executor
     */
    public static ExecutorService newSingleThreadExecutor () {
        MonitoredThreadPoolExecutor monitoredThreadPoolExecutor = new MonitoredThreadPoolExecutor (1, 1,
                                                                                                   0L,
                                                                                                   TimeUnit.MILLISECONDS,
                                                                                                   new MonitoredThreadPoolQueue (
                                                                                                           Integer.MAX_VALUE));

        monitoredThreadPoolExecutor.initialize ();
        return new FinalizableDelegatedExecutorService (monitoredThreadPoolExecutor);
    }

    /**
     * Creates an Executor that uses a single worker thread operating
     * off an unbounded queue, and uses the provided ThreadFactory to
     * create a new thread when needed. Unlike the otherwise
     * equivalent {@code newFixedThreadPool(1, threadFactory)} the
     * returned executor is guaranteed not to be reconfigurable to use
     * additional threads.
     *
     * @param threadFactory the factory to use when creating new
     *                      threads
     *
     * @return the newly created single-threaded Executor
     * @throws NullPointerException if threadFactory is null
     */
    public static ExecutorService newSingleThreadExecutor (ThreadFactory threadFactory) {
        MonitoredThreadPoolExecutor monitoredThreadPoolExecutor = new MonitoredThreadPoolExecutor (1, 1,
                                                                                                   0L,
                                                                                                   TimeUnit.MILLISECONDS,
                                                                                                   new MonitoredThreadPoolQueue (
                                                                                                           Integer.MAX_VALUE),
                                                                                                   threadFactory);

        monitoredThreadPoolExecutor.initialize ();


        return new FinalizableDelegatedExecutorService (monitoredThreadPoolExecutor);
    }

    /**
     * Creates a thread pool that creates new threads as needed, but
     * will reuse previously constructed threads when they are
     * available.  These pools will typically improve the performance
     * of programs that execute many short-lived asynchronous tasks.
     * Calls to {@code execute} will reuse previously constructed
     * threads if available. If no existing thread is available, a new
     * thread will be created and added to the pool. Threads that have
     * not been used for sixty seconds are terminated and removed from
     * the cache. Thus, a pool that remains idle for long enough will
     * not consume any resources. Note that pools with similar
     * properties but different details (for example, timeout parameters)
     * may be created using {@link ThreadPoolExecutor} constructors.
     *
     * @return the newly created thread pool
     */
    public static ExecutorService newCachedThreadPool () {
        MonitoredThreadPoolExecutor monitoredThreadPoolExecutor = new MonitoredThreadPoolExecutor (0, Integer.MAX_VALUE,
                                                                                                   60L,
                                                                                                   TimeUnit.SECONDS,
                                                                                                   new SynchronousQueue<Runnable> ());

        monitoredThreadPoolExecutor.initialize ();

        return monitoredThreadPoolExecutor;
    }

    /**
     * Creates a thread pool that creates new threads as needed, but
     * will reuse previously constructed threads when they are
     * available, and uses the provided
     * ThreadFactory to create new threads when needed.
     *
     * @param threadFactory the factory to use when creating new threads
     *
     * @return the newly created thread pool
     * @throws NullPointerException if threadFactory is null
     */
    public static ExecutorService newCachedThreadPool (ThreadFactory threadFactory) {
        MonitoredThreadPoolExecutor monitoredThreadPoolExecutor = new MonitoredThreadPoolExecutor (0, Integer.MAX_VALUE,
                                                                                                   60L,
                                                                                                   TimeUnit.SECONDS,
                                                                                                   new SynchronousQueue<Runnable> (),
                                                                                                   threadFactory);

        monitoredThreadPoolExecutor.initialize ();

        return monitoredThreadPoolExecutor;
    }

    public static ThreadPoolExecutor newThreadPoolExecutor (int corePoolSize, int maximumPoolSize) {
        return newThreadPoolExecutor (corePoolSize, maximumPoolSize, Executors.defaultThreadFactory ());
    }

    public static ThreadPoolExecutor newThreadPoolExecutor (int corePoolSize, int maximumPoolSize,
                                                            ThreadFactory threadFactory) {
        MonitoredThreadPoolExecutor monitoredThreadPoolExecutor
                = new MonitoredThreadPoolExecutor (corePoolSize,
                                                   maximumPoolSize, 240,
                                                   TimeUnit.SECONDS,
                                                   new MonitoredThreadPoolQueue (5000),
                                                   threadFactory);

        monitoredThreadPoolExecutor.initialize ();

        return monitoredThreadPoolExecutor;
    }

    public static ThreadPoolExecutor newThreadPoolExecutor (int corePoolSize, int maximumPoolSize, int capacity,
                                                            ThreadFactory threadFactory,
                                                            ReporttingRejectedExecutionHandler.Policy policy) {
        MonitoredThreadPoolExecutor monitoredThreadPoolExecutor
                = new MonitoredThreadPoolExecutor (corePoolSize,
                                                   maximumPoolSize, 240,
                                                   TimeUnit.SECONDS,
                                                   new ArrayBlockingQueue (capacity),
                                                   threadFactory,
                                                   new ReporttingRejectedExecutionHandler (policy));

        monitoredThreadPoolExecutor.initialize ();

        return monitoredThreadPoolExecutor;
    }

    /**
     * A wrapper class that exposes only the ExecutorService methods
     * of an ExecutorService implementation.
     */
    static class DelegatedExecutorService extends AbstractExecutorService {
        private final ExecutorService e;

        DelegatedExecutorService (ExecutorService executor) {
            e = executor;
        }

        @Override
        public void execute (Runnable command) {
            e.execute (command);
        }

        @Override
        public void shutdown () {
            e.shutdown ();
        }

        @Override
        public List<Runnable> shutdownNow () {
            return e.shutdownNow ();
        }

        @Override
        public boolean isShutdown () {
            return e.isShutdown ();
        }

        @Override
        public boolean isTerminated () {
            return e.isTerminated ();
        }

        @Override
        public boolean awaitTermination (long timeout, TimeUnit unit)
                throws InterruptedException {
            return e.awaitTermination (timeout, unit);
        }

        @Override
        public Future<?> submit (Runnable task) {
            return e.submit (task);
        }

        @Override
        public <T> Future<T> submit (Callable<T> task) {
            return e.submit (task);
        }

        @Override
        public <T> Future<T> submit (Runnable task, T result) {
            return e.submit (task, result);
        }

        @Override
        public <T> List<Future<T>> invokeAll (Collection<? extends Callable<T>> tasks)
                throws InterruptedException {
            return e.invokeAll (tasks);
        }

        @Override
        public <T> List<Future<T>> invokeAll (Collection<? extends Callable<T>> tasks,
                                              long timeout, TimeUnit unit)
                throws InterruptedException {
            return e.invokeAll (tasks, timeout, unit);
        }

        @Override
        public <T> T invokeAny (Collection<? extends Callable<T>> tasks)
                throws InterruptedException, ExecutionException {
            return e.invokeAny (tasks);
        }

        @Override
        public <T> T invokeAny (Collection<? extends Callable<T>> tasks,
                                long timeout, TimeUnit unit)
                throws InterruptedException, ExecutionException, TimeoutException {
            return e.invokeAny (tasks, timeout, unit);
        }
    }

    static class FinalizableDelegatedExecutorService
            extends DelegatedExecutorService {
        FinalizableDelegatedExecutorService (ExecutorService executor) {
            super (executor);
        }

        @Override
        protected void finalize () {
            super.shutdown ();
        }
    }

    /**
     * A wrapper class that exposes only the ScheduledExecutorService
     * methods of a ScheduledExecutorService implementation.
     */
    static class DelegatedScheduledExecutorService
            extends DelegatedExecutorService
            implements ScheduledExecutorService {
        private final ScheduledExecutorService e;

        DelegatedScheduledExecutorService (ScheduledExecutorService executor) {
            super (executor);
            e = executor;
        }

        @Override
        public ScheduledFuture<?> schedule (Runnable command, long delay, TimeUnit unit) {
            return e.schedule (command, delay, unit);
        }

        @Override
        public <V> ScheduledFuture<V> schedule (Callable<V> callable, long delay, TimeUnit unit) {
            return e.schedule (callable, delay, unit);
        }

        @Override
        public ScheduledFuture<?> scheduleAtFixedRate (Runnable command, long initialDelay, long period,
                                                       TimeUnit unit) {
            return e.scheduleAtFixedRate (command, initialDelay, period, unit);
        }

        @Override
        public ScheduledFuture<?> scheduleWithFixedDelay (Runnable command, long initialDelay, long delay,
                                                          TimeUnit unit) {
            return e.scheduleWithFixedDelay (command, initialDelay, delay, unit);
        }
    }
}

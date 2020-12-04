package org.liujk.java.framework.base.utils.lang.beans.reference;

import com.google.common.base.FinalizableReference;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FinalizableReferenceQueue extends ReferenceQueue<Object> {

    private static final Object LOCK = new Object();

    private static final ReadWriteLock THREAD_LOCK = new ReentrantReadWriteLock();

    private static final Lock READ_LOCK = THREAD_LOCK.readLock();

    private static final Lock WRITE_LOCK = THREAD_LOCK.writeLock();

    private static final Set<CleanUpThread> THREADS = Collections.newSetFromMap(new WeakHashMap<CleanUpThread, Boolean>());

    private static int i;

    private WeakReference<CleanUpThread> threadReference = null;


    public FinalizableReferenceQueue() {
        this(getThreadName());
    }

    private static String getThreadName() {
        synchronized (LOCK) {
            return "FinalizableReferenceQueue#" + ++i;
        }
    }

    public FinalizableReferenceQueue(String name) {
        CleanUpThread thread = newCleanUpThread(name);
        this.threadReference = new WeakReference<CleanUpThread>(thread);
        start(thread);
    }


    protected CleanUpThread newCleanUpThread(String name) {
        CleanUpThread thread = new CleanUpThread(name, this);
        return thread;
    }

    public CleanUpThread getCleanUpThread() {
        return this.threadReference == null ? null : this.threadReference.get();
    }

    protected void cleanUp(Reference<?> reference) {
        try {
            ((FinalizableReference) reference).finalizeReferent();
        } catch (Throwable t) {

        }
    }

    protected void start(CleanUpThread thread) {
        thread.setDaemon(true);
        thread.start();
        WRITE_LOCK.lock();
        try {
            THREADS.add(thread);
        } finally {
            WRITE_LOCK.unlock();
        }
    }

    public static FinalizableReferenceQueue getInstance() {
        return FinalizableReferenceQueueInstance.INSTANCE;
    }

    private static class FinalizableReferenceQueueInstance {
        private static final FinalizableReferenceQueue INSTANCE
                = new FinalizableReferenceQueue("FinalizableReferenceQueue");
    }


    public static class CleanUpThread extends Thread {

        private WeakReference<FinalizableReferenceQueue> queueRef;

        private CleanUpThread(String name, FinalizableReferenceQueue finalizableReferenceQueue) {
            super(name);
            this.queueRef = new WeakReference<FinalizableReferenceQueue>(finalizableReferenceQueue);
        }

        /**
         * 得到当前线程监视的FinalizableReferenceQueue
         *
         * @return
         */
        public FinalizableReferenceQueue getFinalizableReferenceQueue() {
            return this.queueRef == null ? null : this.queueRef.get();
        }

        @Override
        public void run() {
            try {
                while (true) {
                    try {
                        FinalizableReferenceQueue queue = getFinalizableReferenceQueue();
                        if (queue == null) {
                            return;
                        }
                        queue.cleanUp(queue.remove());
                    } catch (InterruptedException e) {
                        Thread.interrupted();
                        this.queueRef = null;
                        return;
                    }
                }
            } finally {
                WRITE_LOCK.lock();
                try {
                    THREADS.remove(this);
                } finally {
                    WRITE_LOCK.unlock();
                }
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(queueRef);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            CleanUpThread thread = (CleanUpThread) obj;
            return Objects.equals(queueRef, thread.queueRef);
        }
    }

}

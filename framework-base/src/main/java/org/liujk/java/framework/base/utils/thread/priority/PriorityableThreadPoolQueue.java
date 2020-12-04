package org.liujk.java.framework.base.utils.thread.priority;


import org.liujk.java.framework.base.utils.thread.MonitoredThreadPoolExecutor;
import org.liujk.java.framework.base.utils.thread.MonitoredThreadPoolQueue;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;


public class PriorityableThreadPoolQueue extends AbstractQueue<Runnable>
        implements BlockingQueue<Runnable>, java.io.Serializable {

    private MonitoredThreadPoolQueue highestPriorityQueue;
    private MonitoredThreadPoolQueue normalPriorityQueue;
    private MonitoredThreadPoolQueue lowestPriorityQueue;

    private static Random random = new Random ();

    public PriorityableThreadPoolQueue (int capacity) {
        highestPriorityQueue = new MonitoredThreadPoolQueue (capacity);
        normalPriorityQueue = new MonitoredThreadPoolQueue (capacity);
        lowestPriorityQueue = new MonitoredThreadPoolQueue (capacity);
    }

    public void setParent (MonitoredThreadPoolExecutor executor) {
        highestPriorityQueue.setParent (executor);
        normalPriorityQueue.setParent (executor);
        lowestPriorityQueue.setParent (executor);
    }

    @Override
    public Iterator<Runnable> iterator () {
        return new MultiIterator (highestPriorityQueue, normalPriorityQueue, lowestPriorityQueue);
    }

    @Override
    public int size () {
        return highestPriorityQueue.size () + normalPriorityQueue.size () + lowestPriorityQueue.size ();
    }

    @Override
    public boolean offer (Runnable runnable) {
        return selectedQueue (runnable).offer (runnable);
    }

    @Override
    public void put (Runnable runnable) throws InterruptedException {
        selectedQueue (runnable).put (runnable);
    }

    @Override
    public boolean offer (Runnable runnable, long timeout, TimeUnit unit) throws InterruptedException {
        return selectedQueue (runnable).offer (runnable, timeout, unit);
    }

    @Override
    public Runnable take () throws InterruptedException {
        return selectedQueue ().take ();
    }

    @Override
    public Runnable poll (long timeout, TimeUnit unit) throws InterruptedException {
        return selectedQueue ().poll (timeout, unit);
    }

    @Override
    public int remainingCapacity () {
        return selectedQueue ().remainingCapacity ();
    }

    @Override
    public int drainTo (Collection<? super Runnable> c) {
        return selectedQueue ().drainTo (c);
    }

    @Override
    public int drainTo (Collection<? super Runnable> c, int maxElements) {
        return selectedQueue ().drainTo (c, maxElements);
    }

    @Override
    public Runnable poll () {
        return selectedQueue ().poll ();
    }

    @Override
    public Runnable peek () {
        return selectedQueue ().peek ();
    }

    protected MonitoredThreadPoolQueue selectedQueue (Runnable runnable) {
        MonitoredThreadPoolQueue selectedQueue = normalPriorityQueue;
        if (runnable instanceof Priorityable) {
            Priorityable priorityable = (Priorityable) runnable;
            switch (priorityable.getPriority ()) {
                case HIGHEST:
                    selectedQueue = highestPriorityQueue;
                    break;
                case LOWEST:
                    selectedQueue = lowestPriorityQueue;
                    break;
                default:
                    selectedQueue = normalPriorityQueue;
                    break;
            }
        }
        return selectedQueue;
    }

    protected MonitoredThreadPoolQueue selectedQueue () {
        MonitoredThreadPoolQueue selectedQueue = normalPriorityQueue;

        if (highestPriorityQueue.isEmpty () && lowestPriorityQueue.isEmpty ()) {
            selectedQueue = normalPriorityQueue;
        } else if (highestPriorityQueue.isEmpty ()) {
            if (normalPriorityQueue.isEmpty () || random.nextInt (6) < 2) {
                selectedQueue = lowestPriorityQueue;
            }
        } else if (lowestPriorityQueue.isEmpty ()) {
            if (normalPriorityQueue.isEmpty () || random.nextInt (6) < 4) {
                selectedQueue = highestPriorityQueue;
            }
        } else {
            int pos = random.nextInt (12);
            if (pos >= 5) {
                //  6,7,8,9,10,11
                selectedQueue = highestPriorityQueue;
            } else if (pos < 2) {
                //0,1
                selectedQueue = lowestPriorityQueue;
            }
            // 2,3,4,5
        }
        return selectedQueue;
    }


    public class MultiIterator<T> implements Iterator<T> {
        Iterable<T>[] iterables;
        private int index = 0;

        public MultiIterator (Iterable<T>... iterables) {
            this.iterables = iterables;
        }

        @Override
        public boolean hasNext () {
            if (index < iterables.length) {
                if (iterables[index].iterator ().hasNext ()) {
                    return true;
                } else {
                    index++;
                    return hasNext ();
                }
            }
            return false;
        }

        @Override
        public T next () {
            if (index < iterables.length) {
                if (iterables[index].iterator ().hasNext ()) {
                    return iterables[index].iterator ().next ();
                } else {
                    index++;
                    return next ();
                }
            }
            return null;
        }
    }

}

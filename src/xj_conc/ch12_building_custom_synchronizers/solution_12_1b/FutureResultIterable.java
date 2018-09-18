/*
 * This class forms part of the Java Concurrency Course by
 * Cretesoft Limited and may not be distributed without 
 * written consent.
 *
 * Copyright 2011, Heinz Kabutz, All rights reserved.
 */
package xj_conc.ch12_building_custom_synchronizers.solution_12_1b;

import net.jcip.annotations.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;

@ThreadSafe
public class FutureResultIterable<V> implements Iterable<Future<V>> {
    private final ExecutorService delegate;
    private final CompletionService<V> service;
    private final ExecutorService resultFetcher = Executors.newSingleThreadExecutor();

    private final AtomicInteger numberOfSubmittedTasks = new AtomicInteger(0);
    private final Lock lock = new ReentrantLock();
    private final Condition hasCompletedJobs = lock.newCondition();
    @GuardedBy("lock")
    private final List<Future<V>> completedJobs = new ArrayList<>();

    /**
     * DO NOT CHANGE.
     */
    public FutureResultIterable() {
        delegate = Executors.newCachedThreadPool();
        service = new ExecutorCompletionService<>(delegate);
    }

    /**
     * DO NOT CHANGE.
     */
    public void submit(Callable<V> task) {
        service.submit(task);
        if (numberOfSubmittedTasks.getAndIncrement() == 0) {
            startResultFetcherThread(); // to avoid starting it in the constructor
        }
    }

    private void startResultFetcherThread() {
        resultFetcher.submit(new Runnable() {
            private boolean wasInterrupted = false;

            public void run() {
                while (!wasInterrupted || hasMoreResults()) {
                    fetchNextResult();
                }
            }

            private boolean hasMoreResults() {
                lock.lock();
                try {
                    return completedJobs.size() < numberOfSubmittedTasks.get();
                } finally {
                    lock.unlock();
                }
            }

            private void fetchNextResult() {
                try {
                    Future<V> future = service.take();
                    lock.lock();
                    try {
                        completedJobs.add(future);
                        hasCompletedJobs.signalAll();
                    } finally {
                        lock.unlock();
                    }
                } catch (InterruptedException e) {
                    wasInterrupted = true;
                }
            }
        });
    }

    // the iterator returned from this method is *not* thread-safe
    public Iterator<Future<V>> iterator() {
        return new Iterator<Future<V>>() {
            private int nextJob = 0;

            public boolean hasNext() {
                return nextJob < numberOfSubmittedTasks.get();
            }

            public Future<V> next() {
                if (!hasNext()) throw new NoSuchElementException();
                lock.lock();
                try {
                    while (!(nextJob < completedJobs.size())) {
                        hasCompletedJobs.awaitUninterruptibly();
                    }
                    return completedJobs.get(nextJob++);
                } finally {
                    lock.unlock();
                }
            }

            public void remove() {
                throw new UnsupportedOperationException("not supported");
            }
        };
    }

    /**
     * DO NOT CHANGE.
     */
    public void shutdown() {
        delegate.shutdown();
        resultFetcher.shutdownNow();
    }

    /**
     * DO NOT CHANGE.
     */
    public boolean isTerminated() {
        return resultFetcher.isTerminated() && delegate.isTerminated();
    }
}

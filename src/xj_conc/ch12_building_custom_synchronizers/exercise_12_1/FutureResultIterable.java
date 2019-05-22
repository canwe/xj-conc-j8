package xj_conc.ch12_building_custom_synchronizers.exercise_12_1;

import net.jcip.annotations.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@ThreadSafe
public class FutureResultIterable<V> implements Iterable<Future<V>> {

    private final ExecutorService delegate;
    private final CompletionService<V> service;
    private final ExecutorService resultFetcher = Executors.newSingleThreadExecutor();
    private final AtomicInteger numberOfSubmittedTasks = new AtomicInteger(0);
    @GuardedBy("lock") private final List<Future<V>> completedJobs = new ArrayList<>();

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

    private final Lock lock = new ReentrantLock();
    private final Condition hasJob = lock.newCondition();

    private void startResultFetcherThread() {
        resultFetcher.submit(new Runnable() {

            private boolean stop = false;

            private boolean hasMoreTasks() {
                lock.lock();
                try {
                    return completedJobs.size() < numberOfSubmittedTasks.get();
                } finally {
                    lock.unlock();
                }
            }
            @Override
            public void run() {
                while (!stop || hasMoreTasks()) {
                    try {
                        Future<V> job = service.take();
                        lock.lock();
                        try {
                            completedJobs.add(job);
                            hasJob.signalAll();
                        } finally {
                            lock.unlock();
                        }
                    } catch (InterruptedException e) {
                        stop = true;
                    }
                }
            }
        });
    }

    public Iterator<Future<V>> iterator() {
        return new Iterator<Future<V>>() {
            private int pos;

            public boolean hasNext() {
                return Integer.compare(pos, numberOfSubmittedTasks.get()) == -1;
            }

            public Future<V> next() {
                if (!hasNext()) throw new NoSuchElementException();
                lock.lock();
                // BLOCK-UNTIL: pos < completedJobs.size()
                try {
                    while (!(pos < completedJobs.size())) {
                        hasJob.awaitUninterruptibly();
                    }
                    return completedJobs.get(pos++);
                } finally {
                    lock.unlock();
                }
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

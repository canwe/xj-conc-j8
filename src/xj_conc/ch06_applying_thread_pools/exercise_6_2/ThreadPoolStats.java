package xj_conc.ch06_applying_thread_pools.exercise_6_2;

import java.lang.ref.*;
import java.util.concurrent.*;

/**
 * TODO: Implement all the incomplete methods, delegating to the thread pool
 * TODO: returned by the getPool() method.
 */
public class ThreadPoolStats implements ThreadPoolStatsMBean {
    private final WeakReference<ThreadPoolExecutor> ref;

    /**
     * DO NOT CHANGE.
     */
    public ThreadPoolStats(ThreadPoolExecutor pool) {
        ref = new WeakReference<>(pool);
    }

    /**
     * DO NOT CHANGE.
     */
    private ThreadPoolExecutor getPool() {
        ThreadPoolExecutor pool = ref.get();
        if (pool == null) {
            ThreadPoolStatsManager.unregister(this);
            throw new IllegalStateException("Pool has already been garbage collected");
        }
        return pool;
    }

    /**
     * DO NOT CHANGE.
     */
    public void shutdown() {
        getPool().shutdown();
    }

    public Class<? extends RejectedExecutionHandler> getRejectedExecutionHandlerType() {
        throw new UnsupportedOperationException("TODO");
    }

    public void setCorePoolSize(int corePoolSize) {
        throw new UnsupportedOperationException("TODO");
    }

    public int getCorePoolSize() {
        throw new UnsupportedOperationException("TODO");
    }

    public int prestartAllCoreThreads() {
        throw new UnsupportedOperationException("TODO");
    }

    public long getKeepAliveTimeInMilliseconds() {
        throw new UnsupportedOperationException("TODO");
    }

    public Class<? extends BlockingQueue> getQueueType() {
        throw new UnsupportedOperationException("TODO");
    }

    public int getPoolSize() {
        throw new UnsupportedOperationException("TODO");
    }

    public int getActiveCount() {
        throw new UnsupportedOperationException("TODO");
    }

    public int getLargestPoolSize() {
        throw new UnsupportedOperationException("TODO");
    }

    public long getTaskCount() {
        throw new UnsupportedOperationException("TODO");
    }

    public long getCompletedTaskCount() {
        throw new UnsupportedOperationException("TODO");
    }
}
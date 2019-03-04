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
        return getPool().getRejectedExecutionHandler().getClass();
    }

    public void setCorePoolSize(int corePoolSize) {
        getPool().setCorePoolSize(corePoolSize);
    }

    public int getCorePoolSize() {
        return getPool().getCorePoolSize();
    }

    public int prestartAllCoreThreads() {
        return getPool().prestartAllCoreThreads();
    }

    public long getKeepAliveTimeInMilliseconds() {
        return getPool().getKeepAliveTime(TimeUnit.MILLISECONDS);
    }

    public Class<? extends BlockingQueue> getQueueType() {
        return getPool().getQueue().getClass();
    }

    public int getPoolSize() {
        return getPool().getPoolSize();
    }

    public int getActiveCount() {
        return getPool().getActiveCount();
    }

    public int getLargestPoolSize() {
        return getPool().getLargestPoolSize();
    }

    public long getTaskCount() {
        return getPool().getTaskCount();
    }

    public long getCompletedTaskCount() {
        return getPool().getCompletedTaskCount();
    }
}

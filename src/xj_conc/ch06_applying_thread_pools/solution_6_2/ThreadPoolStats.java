package xj_conc.ch06_applying_thread_pools.solution_6_2;

import java.lang.ref.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * A complete mbean that allows us to manage a thread pool using JConsole or
 * any MBean viewer.  It includes the additional properties that were left out
 * for the exercise.
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

    public List<Runnable> shutdownNow() {
        return getPool().shutdownNow();
    }

    public boolean isShutdown() {
        return getPool().isShutdown();
    }

    public boolean isTerminating() {
        return getPool().isTerminating();
    }

    public boolean isTerminated() {
        return getPool().isTerminated();
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

    public boolean prestartCoreThread() {
        return getPool().prestartCoreThread();
    }

    public int prestartAllCoreThreads() {
        return getPool().prestartAllCoreThreads();
    }

    public boolean allowsCoreThreadTimeOut() {
        return getPool().allowsCoreThreadTimeOut();
    }

    public void allowCoreThreadTimeOut(boolean value) {
        getPool().allowCoreThreadTimeOut(value);
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        getPool().setMaximumPoolSize(maximumPoolSize);
    }

    public int getMaximumPoolSize() {
        return getPool().getMaximumPoolSize();
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
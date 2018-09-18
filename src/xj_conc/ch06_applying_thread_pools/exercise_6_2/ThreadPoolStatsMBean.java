package xj_conc.ch06_applying_thread_pools.exercise_6_2;

import java.util.concurrent.*;

/**
 * DO NOT CHANGE.
 */
public interface ThreadPoolStatsMBean {
    void shutdown();

    int prestartAllCoreThreads();

    Class<? extends RejectedExecutionHandler> getRejectedExecutionHandlerType();

    Class<? extends BlockingQueue> getQueueType();

    void setCorePoolSize(int corePoolSize);

    int getCorePoolSize();

    long getKeepAliveTimeInMilliseconds();

    int getPoolSize();

    int getActiveCount();

    int getLargestPoolSize();

    long getTaskCount();

    long getCompletedTaskCount();
}
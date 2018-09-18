package xj_conc.ch06_applying_thread_pools.solution_6_2;

import java.util.*;
import java.util.concurrent.*;

/**
 * In the solution, I included additional properties, such as
 * allowsCoreThreadTimeOut().  I left this out in the exercise, because most of
 * the code is repetitive.
 */
public interface ThreadPoolStatsMBean {
    void shutdown();

    List<Runnable> shutdownNow();

    boolean isShutdown();

    boolean isTerminating();

    boolean isTerminated();

    Class<? extends RejectedExecutionHandler> getRejectedExecutionHandlerType();

    void setCorePoolSize(int corePoolSize);

    int getCorePoolSize();

    boolean prestartCoreThread();

    int prestartAllCoreThreads();

    boolean allowsCoreThreadTimeOut();

    void allowCoreThreadTimeOut(boolean value);

    void setMaximumPoolSize(int maximumPoolSize);

    int getMaximumPoolSize();

    long getKeepAliveTimeInMilliseconds();

    Class<? extends BlockingQueue> getQueueType();

    int getPoolSize();

    int getActiveCount();

    int getLargestPoolSize();

    long getTaskCount();

    long getCompletedTaskCount();
}
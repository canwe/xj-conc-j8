package xj_conc.ch06_applying_thread_pools.solution_6_1;

import java.util.concurrent.*;
import java.util.function.*;

/**
 * Return a new ThreadPoolExecutor with the following characteristics:
 * 1. Core pool size of 10, maximum pool size or 100, timeout of 5 seconds
 * 2. Task queue should be bounded queue of 1000.
 * 3. Threads should have a priority of Thread.MAX_PRIORITY-2, be
 * non-daemon and be named by pattern such as: pool-1-thread-1#description
 * 4. If we exceed capacity, let the caller run the job himself.
 * 5. If we shut down, reject new jobs with a RejectedExecutionException
 */
public class ThreadPoolSupplier implements Function<String, ThreadPoolExecutor> {
    public ThreadPoolExecutor apply(String description) {
        return new ThreadPoolExecutor(10, 100,
            5L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000),
            new ThreadFactory() {
                private final ThreadFactory defaultFactory = Executors.defaultThreadFactory();

                public Thread newThread(Runnable r) {
                    Thread t = defaultFactory.newThread(r);
                    t.setName(t.getName() + "#" + description);
                    t.setPriority(Thread.MAX_PRIORITY - 2);
                    return t;
                }
            },
            (r, executor) -> {
                if (executor.isShutdown()) {
                    throw new RejectedExecutionException("pool shut down");
                } else {
                    r.run();
                }
            });
    }
}

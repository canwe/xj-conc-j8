package xj_conc.ch06_applying_thread_pools.solution_6_2;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * DO NOT CHANGE.
 */
public class ThreadPoolStatsTest {
    public static void main(String... args) throws InterruptedException {
        ScheduledThreadPoolExecutor sched =
            (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(1);
        ThreadPoolExecutor fixed = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        ThreadPoolExecutor cached = (ThreadPoolExecutor) Executors.newCachedThreadPool();

        ThreadPoolStatsManager.register(cached, "cached");
        ThreadPoolStatsManager.register(fixed, "fixed");
        ThreadPoolStatsManager.register(sched, "sched");
        ThreadPoolStatsManager.register(
            (ThreadPoolExecutor) Executors.newCachedThreadPool(), "dummy");

        sched.scheduleAtFixedRate(new PoolSubmitter(cached, "cached"), 1, 1, TimeUnit.SECONDS);
        sched.scheduleAtFixedRate(new PoolSubmitter(fixed, "fixed"), 1, 1, TimeUnit.SECONDS);
        TimeUnit.MINUTES.sleep(5);
        fixed.shutdown();
        cached.shutdown();
        sched.shutdown();
    }

    private static class PoolSubmitter implements Runnable {
        private final AtomicInteger count = new AtomicInteger();
        private final ThreadPoolExecutor cached;
        private final String name;

        public PoolSubmitter(ThreadPoolExecutor cached, String name) {
            this.cached = cached;
            this.name = name;
        }

        public void run() {
            cached.submit(() -> System.out.println(name + count.incrementAndGet()));
        }
    }
}

package xj_conc.ch04_task_execution.solution_4_1;

import xj_conc.math.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * In this solution, we simply replace new Thread().start() with submitting
 * Runnable to a Fixed Thread Pool, giving a nice speedup.
 */
public class ParallelFactorizer {
    public int factorizeInParallel(long start, long numbersToCheck)
        throws InterruptedException {
        LongAdder primes = new LongAdder();

        // Change this block of code only:
        AtomicLong next = new AtomicLong(start);
        ExecutorService pool = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors() * 2);
        for (long i = 0; i < numbersToCheck; i++) {
            pool.execute(() -> {
                long number = next.getAndIncrement();
                long[] factors = Factorizer.factor(number);
                if (factors.length == 1) {
                    primes.increment();
                }
            });
        }
        pool.shutdown();
        while (!pool.awaitTermination(1, TimeUnit.SECONDS)) ;

        return primes.intValue();
    }
}

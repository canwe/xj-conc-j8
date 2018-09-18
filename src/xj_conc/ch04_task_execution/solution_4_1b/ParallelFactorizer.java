package xj_conc.ch04_task_execution.solution_4_1b;

import xj_conc.math.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * In this solution, we go one step further by letting each task solve several
 * numbers.
 */
public class ParallelFactorizer {
    private static final int NUMBERS_PER_TASK = 400;

    public int factorizeInParallel(long start, int numbersToCheck)
        throws InterruptedException {
        LongAdder primes = new LongAdder();

        // Change this block of code only:
        AtomicLong next = new AtomicLong(start);
        ExecutorService pool = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors() * 2);
        for (int i = 0; i < numbersToCheck / NUMBERS_PER_TASK; i++) {
            pool.execute(() -> {
                for (int i1 = 0; i1 < NUMBERS_PER_TASK; i1++) {
                    long number = next.getAndIncrement();
                    long[] factors = Factorizer.factor(number);
                    if (factors.length == 1) {
                        primes.increment();
                    }
                }
            });
        }
        pool.shutdown();
        while (!pool.awaitTermination(1, TimeUnit.SECONDS)) ;

        return primes.intValue();
    }
}

package xj_conc.ch04_task_execution.solution_4_1c;

import xj_conc.math.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * For even better scalability, we eliminiate the "hot field" number and instead
 * assign ranges of numbers to each of the tasks.  This could make a difference
 * if the hot field becomes the bottleneck if we run this on a huge number of
 * cores.
 */
public class ParallelFactorizer {
    private static final int NUMBERS_PER_TASK = 400;

    public int factorizeInParallel(long start, int numbersToCheck)
        throws InterruptedException {
        LongAdder primes = new LongAdder();

        // This solution passes in the numbers to check:
        ExecutorService pool = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors() * 2);
        for (int i = 0; i < numbersToCheck; i += NUMBERS_PER_TASK) {
            long from = start + i;
            long until = from + NUMBERS_PER_TASK;
            pool.execute(() -> {
                for (long number = from; number < until; number++) {
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

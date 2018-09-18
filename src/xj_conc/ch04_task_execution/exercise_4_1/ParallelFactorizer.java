package xj_conc.ch04_task_execution.exercise_4_1;

import xj_conc.math.*;

import java.util.concurrent.atomic.*;

/**
 * Instead of constructing a separate thread for each number, we want to rather
 * use a thread pool.  This should hopefully reduce the latency for factorizing
 * one prime, as we do not need to include the start-up time of a thread.
 */
public class ParallelFactorizer {
    public int factorizeInParallel(long start, int numbersToCheck)
        throws InterruptedException {
        LongAdder primes = new LongAdder();

        // Change this block of code only:
        AtomicLong next = new AtomicLong(start);
        Thread[] threads = new Thread[numbersToCheck];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                long number = next.getAndIncrement();
                long[] factors = Factorizer.factor(number);
                if (factors.length == 1) {
                    primes.increment();
                }
            });
            threads[i].start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        return primes.intValue();
    }
}

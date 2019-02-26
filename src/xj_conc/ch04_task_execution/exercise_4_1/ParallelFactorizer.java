package xj_conc.ch04_task_execution.exercise_4_1;

import xj_conc.math.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.*;
import java.util.function.Supplier;

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
        CompletableFuture[] futures = new CompletableFuture[numbersToCheck];
        for (int i = 0; i < numbersToCheck; i++) {
            futures[i] = CompletableFuture.supplyAsync(() -> Factorizer.isPrime(next.getAndIncrement())).thenAccept(isPrime -> {if (isPrime) primes.increment();});
        }
        CompletableFuture.allOf(futures).join();
        return primes.intValue();
    }
}

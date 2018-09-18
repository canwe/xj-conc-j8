package xj_conc.ch04_task_execution.solution_4_2;

import xj_conc.math.*;

import java.util.stream.*;

/**
 * In this solution, we simply replace new Thread().start() with submitting
 * Runnable to a Fixed Thread Pool, giving a nice speedup.
 */
public class ParallelFactorizer {
    public int factorizeInParallel(long start, long numbersToCheck)
        throws InterruptedException {
        return (int) LongStream.range(start, start + numbersToCheck).
            parallel().
            filter(this::isPrime).
            count();
    }

    private boolean isPrime(long number) {
        return Factorizer.factor(number).length == 1;
    }
}

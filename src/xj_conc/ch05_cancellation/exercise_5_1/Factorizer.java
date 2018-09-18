package xj_conc.ch05_cancellation.exercise_5_1;

import java.util.*;

/**
 * TODO: Change factor() to allow another thread to cancel it using interrupt().
 */
public class Factorizer {
    public long[] factor(long number) throws InterruptedException {
        Collection<Long> factors = new ArrayList<>();
        for (long factor = 2; factor <= number; factor++) {
            while (number % factor == 0) {
                factors.add(factor);
                number /= factor;
            }
        }
        long[] result = new long[factors.size()];
        int pos = 0;
        for (Long factor : factors) {
            result[pos++] = factor;
        }
        return result;
    }
}
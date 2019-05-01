package xj_conc.ch07_fork_join.exercise_7_2;

import java.util.Arrays;
import java.util.concurrent.*;
import java.util.stream.*;

/**
 * The NumberSearcher is looking for longs that cannot be
 * represented without loss of precision in a double.  It creates
 * a bunch of random numbers and then uses Fork/Join to count the
 * values.
 * <p>
 * Instead of using Fork/Join, use parallel streams.  Besides
 * making the code easier to understand and shorter, your
 * performance should also improve a bit.
 */
public class NumberSearcher {
    public long findUnrepresentableLongs(int numberCount) {
        LongStream longs  = ThreadLocalRandom.current().
                longs(numberCount, 0, Long.MAX_VALUE >>> 9);
        return longs.parallel().filter(this::isTooLargeForDouble).count();
    }

    private boolean isTooLargeForDouble(long number) {
        return ((long) (double) number) != number;
    }
}

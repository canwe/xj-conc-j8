package xj_conc.ch07_fork_join.exercise_7_2;

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
        long[] longs = new long[numberCount];
        for (int i = 0; i < longs.length; i++) {
            longs[i] = ThreadLocalRandom.current().
                nextLong(0, Long.MAX_VALUE >>> 9);
        }
        ForkJoinPool pool = ForkJoinPool.commonPool();
        return pool.invoke(new UnrepresentableLongCountTask(
            longs, 0, longs.length));
    }

    private boolean isTooLargeForDouble(long number) {
        return ((long) (double) number) != number;
    }

    private class UnrepresentableLongCountTask extends RecursiveTask<Long> {
        private static final int THRESHOLD = 10000;
        private final long[] longs;
        private final int offset;
        private final int length;

        public UnrepresentableLongCountTask(long[] longs, int offset, int length) {
            this.longs = longs;
            this.offset = offset;
            this.length = length;
        }

        protected Long compute() {
            if (length < THRESHOLD) {
                return sequentialSearch();
            }
            int offset_left = offset; // 500_000
            int length_left = length / 2; // 250_000
            int offset_right = offset + length_left; // 750_000
            int length_right = length - length_left; // 250_000
            UnrepresentableLongCountTask left =
                new UnrepresentableLongCountTask(
                    longs, offset_left, length_left);
            UnrepresentableLongCountTask right =
                new UnrepresentableLongCountTask(
                    longs, offset_right, length_right);
            left.fork();
            return right.invoke() + left.join();
        }

        private long sequentialSearch() {
            long result = 0;
            for (int i = offset; i < offset + length; i++) {
                if (isTooLargeForDouble(longs[i])) result++;
            }
            return result;
        }
    }
}

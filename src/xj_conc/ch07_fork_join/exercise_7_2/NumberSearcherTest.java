package xj_conc.ch07_fork_join.exercise_7_2;

import org.junit.*;

import java.io.*;
import java.util.concurrent.*;

import static org.junit.Assert.*;

/**
 * DO NOT CHANGE.
 */
public class NumberSearcherTest {
    /**
     * DO NOT CHANGE.
     */
    @Test
    public void testNumberSearching() throws InterruptedException {
        // warmup
        searchWithNumberSearcher();
        searchWithFJNumberSearcher();

        long time = searchWithNumberSearcher();
        long fjtime = searchWithFJNumberSearcher();

        System.out.println("Your NumberSearcher time = " + time);
        System.out.println("Old Fork/Join NumberSearcher time = " + fjtime);

        assertTrue("We would expect the stream solution to be a bit faster than the Fork/Join one",
            time * 1.1 < fjtime);
    }

    /**
     * DO NOT CHANGE.
     */
    private long searchWithNumberSearcher() {
        NumberSearcher searcher = new NumberSearcher();
        long time = System.currentTimeMillis();
        long numbers = search(searcher, 100);
        System.out.printf("NumberSearcher numbers = %,d%n", numbers);
        time = System.currentTimeMillis() - time;
        return time;
    }

    /**
     * DO NOT CHANGE.
     */
    private long searchWithFJNumberSearcher() {
        FJNumberSearcher fjsearcher = new FJNumberSearcher();
        long fjtime = System.currentTimeMillis();
        long numbers = search(fjsearcher, 100);
        System.out.printf("Old Fork/Join NumberSearcher numbers = %,d%n", numbers);
        fjtime = System.currentTimeMillis() - fjtime;
        return fjtime;
    }

    /**
     * DO NOT CHANGE.
     */
    private long search(NumberSearcher searcher, int repeats) {
        long numbers = 0;
        for (int i = 0; i < repeats; i++) {
            numbers += search(searcher);
        }
        return numbers;
    }

    /**
     * DO NOT CHANGE.
     */
    private long search(NumberSearcher searcher) {
        return searcher.findUnrepresentableLongs(10_000_000);
    }

    /**
     * DO NOT CHANGE.
     */
    private long search(FJNumberSearcher searcher) {
        return searcher.findUnrepresentableLongs(10_000_000);
    }

    /**
     * DO NOT CHANGE.
     */
    private long search(FJNumberSearcher searcher, int repeats) {
        long numbers = 0;
        for (int i = 0; i < repeats; i++) {
            numbers += search(searcher);
        }
        return numbers;
    }

    /**
     * DO NOT CHANGE.
     */
    private static class FJNumberSearcher {
        public long findUnrepresentableLongs(int numberCount) {
            long[] longs = new long[numberCount];
            for (int i = 0; i < longs.length; i++) {
                longs[i] = ThreadLocalRandom.current().
                    nextLong(0, Long.MAX_VALUE >>> 9);
            }
            ForkJoinPool pool = ForkJoinPool.commonPool();
            return pool.invoke(new UnrepresentableLongAction(
                longs, 0, longs.length));
        }

        private boolean isTooLargeForDouble(long d) {
            return ((long) (double) d) != d;
        }

        private class UnrepresentableLongAction extends RecursiveTask<Long> {
            private static final int THRESHOLD = 10000;
            private final long[] longs;
            private final int start;
            private final int length;

            public UnrepresentableLongAction(long[] longs, int start, int length) {
                this.longs = longs;
                this.start = start;
                this.length = length;
            }

            protected Long compute() {
                if (length < THRESHOLD) {
                    return sequentialSearch();
                }
                int start_left = start;
                int length_left = length / 2;
                int start_right = start + length_left;
                int length_right = length - length_left;
                UnrepresentableLongAction left =
                    new UnrepresentableLongAction(longs, start_left, length_left);
                UnrepresentableLongAction right =
                    new UnrepresentableLongAction(longs, start_right, length_right);
                left.fork();
                return right.invoke() + left.join();
            }

            private long sequentialSearch() {
                long result = 0;
                for (int i = start; i < start + length; i++) {
                    if (isTooLargeForDouble(longs[i])) result++;
                }
                return result;
            }
        }
    }

    /**
     * DO NOT CHANGE.
     */
    @Test
    public void testClassSize() throws IOException {
        LineNumberReader lnr = new LineNumberReader(
            new FileReader("src/xj_conc/ch07_fork_join/exercise_7_2/NumberSearcher.java"));
        while (lnr.readLine() != null) ;
        assertTrue("NumberSearcher should be less than 30 LOC now, but it is " + lnr.getLineNumber(),
            lnr.getLineNumber() < 30);
    }
}

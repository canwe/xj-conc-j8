package xj_conc.util;

import static org.junit.Assert.*;

public class ByteWatcherRegressionTestHelper {
    private final ByteWatcherSingleThread bw;

    public ByteWatcherRegressionTestHelper(Thread thread) {
        bw = new ByteWatcherSingleThread(thread);
    }

    public ByteWatcherRegressionTestHelper() {
        this(Thread.currentThread());
    }

    public void testAllocationNotExceeded(
        Runnable job, long limit) {
        bw.reset();
        job.run();
        long size = bw.calculateAllocations();
        assertTrue(String.format("exceeded limit: %d using: %d%n",
            limit, size), size <= limit);
    }
}

package xj_conc.ch06_applying_thread_pools.exercise_6_3;

import org.junit.*;

import java.util.concurrent.*;

import static junit.framework.Assert.*;

/**
 * DO NOT CHANGE.
 */
public class ThreadPoolFactoryTest {
    private static final int MAXIMUM_THREADS = 2000;
    private final ThreadPoolFactory factory = new ThreadPoolFactory();

    /**
     * Wait time is very high, meaning that it almost always waits.
     */
    @Test
    public void testHugeThreadPool() {
        ThreadPoolExecutor service = (ThreadPoolExecutor)
            factory.newFixedThreadPool(32, 1.0, 0.9999999999, MAXIMUM_THREADS);
        assertEquals(MAXIMUM_THREADS, service.getCorePoolSize());
    }

    /**
     * 0.1% of the time, the threads will be in the waiting state.  System running
     * at 100% utilization.
     */
    @Test
    public void testCPUIntensiveThreadPool() {
        ThreadPoolExecutor service = (ThreadPoolExecutor)
            factory.newFixedThreadPool(32, 1.0, 0.001, MAXIMUM_THREADS);
        assertEquals(33, service.getCorePoolSize());
    }

    /**
     * 0.1% of the time, the threads will be in the waiting state.  System running
     * at 70% utilization.
     */
    @Test
    public void testCPUIntensiveThreadPoolWithReserveCapacity() {
        ThreadPoolExecutor service = (ThreadPoolExecutor)
            factory.newFixedThreadPool(32, 0.7, 0.001, MAXIMUM_THREADS);
        assertEquals(23, service.getCorePoolSize());
    }

    /**
     * 98% of the time, the threads will be in the waiting state.  System running
     * at 100% utilization.
     */
    @Test
    public void testIOIntensiveThreadPool() {
        ThreadPoolExecutor service = (ThreadPoolExecutor)
            factory.newFixedThreadPool(32, 1.0, 0.98, MAXIMUM_THREADS);
        assertEquals(1600, service.getCorePoolSize());
    }

    /**
     * 98% of the time, the threads will be in the waiting state.  System running
     * at 50% utilization.
     */
    @Test
    public void testIOIntensiveThreadPoolWithReserveCapacity() {
        ThreadPoolExecutor service = (ThreadPoolExecutor)
            factory.newFixedThreadPool(32, 0.7, 0.98, MAXIMUM_THREADS);
        assertEquals(1120, service.getCorePoolSize());
    }

    /**
     * This should throw an IllegalArgumentException - waitTime cannot be less
     * than zero.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUtilizationUnderflow() {
        factory.newFixedThreadPool(1, -0.01, 0.5, MAXIMUM_THREADS);
    }

    /**
     * This should throw an IllegalArgumentException - utilization cannot be less
     * than zero.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUtilizationOverflow() {
        factory.newFixedThreadPool(1, 1.01, 0.5, MAXIMUM_THREADS);
    }

    /**
     * This should throw an IllegalArgumentException - waitTime cannot be less
     * than zero.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testWaitTimeUnderflow() {
        factory.newFixedThreadPool(1, 0.5, -0.01);
    }

    /**
     * This should throw an IllegalArgumentException - utilization cannot be less
     * than zero.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testWaitTimeOverflow() {
        factory.newFixedThreadPool(1, 0.5, 1.01);
    }

    /**
     * This should throw an IllegalArgumentException - number of CPUs should be
     * greater than zero.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCPUsUnderflow() {
        factory.newFixedThreadPool(0, 0.5, 0.5);
    }

    /**
     * This should throw an IllegalArgumentException - maximum number of threads
     * should be greater than zero.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMaximumThreadsUnderflow() {
        factory.newFixedThreadPool(1, 0.5, 0.5, 0);
    }
}

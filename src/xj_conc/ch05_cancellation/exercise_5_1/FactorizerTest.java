package xj_conc.ch05_cancellation.exercise_5_1;

import org.junit.*;

import java.util.*;
import java.util.concurrent.*;

import static junit.framework.Assert.*;

/**
 * DO NOT CHANGE.
 */
public class FactorizerTest {
    @Test
    public void testIntPrimeFactoring() throws InterruptedException {
        check(2, 2, 11);
        check(2);
        check(3);
        check(7, 11);
        check(101);
        check(127);
    }

    @Test
    public void testLargeNumberPrimeFactoring() throws InterruptedException {
        check(173, 739, 967, 4_870_693);
        check(7, 29, 631, 809, 14_481_869);
        check(223, 647, 10651, 30_735_589);
    }

    @Test
    public void testCancellation() throws InterruptedException {
        Callable<long[]> factorLargeNumber = () -> {
            Factorizer factorizer = new Factorizer();
            try {
                return factorizer.factor(Integer.MAX_VALUE);
            } catch (InterruptedException e) {
                if (Thread.currentThread().isInterrupted()) {
                    throw new AssertionError("A thread should not be both" +
                        " interrupted and throwing an InterruptedException");
                }
                throw e;
            }
        };
        ExecutorService pool = Executors.newCachedThreadPool();
        Future<long[]> future = pool.submit(factorLargeNumber);
        Thread.sleep(500);
        pool.shutdownNow(); // this will also interrupt the factoring thread
        assertTrue("Factorizer did not shut down within 500ms",
            pool.awaitTermination(500, TimeUnit.MILLISECONDS));
        try {
            long[] result = future.get();
            System.out.println("Result is " + Arrays.toString(result));
        } catch (ExecutionException e) {
            try {
                throw e.getCause();
            } catch (InterruptedException | CancellationException ex) {
                System.out.println("Received a " + e.getCause());
            } catch (Throwable throwable) {
                fail(throwable.toString());
            }
        }
    }

    @Test
    public void testNoCancelation() throws InterruptedException {
        System.out.println("FactorizerTest.testNoCancelation");
        long time = System.currentTimeMillis();
        check(Integer.MAX_VALUE);
        time = System.currentTimeMillis() - time;
        System.out.println("time = " + time);
    }

    private void check(long... nums) throws InterruptedException {
        long number = 1;
        for (long i : nums) {
            number *= i;
        }
        Factorizer factorizer = new Factorizer();
        long[] result = factorizer.factor(number);
        assertTrue(Arrays.equals(nums, result));
    }
}

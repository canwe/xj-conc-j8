package xj_conc.ch09_testing_concurrent_programs.exercise_9_1;

import org.junit.*;

import java.util.*;
import java.util.concurrent.*;

import static org.junit.Assert.*;

/**
 * The HandoverQueue is a half-baked queue implementation that needs to be
 * tested.
 * <p>
 * First some correctness tests:
 * <p>
 * 1. isEmpty() == true for a newly constructed HandoverQueue
 * 2. Elements added with offer() are returned with poll() in same order
 * 3. Calling peek() should return the first element without removing it.
 * <p>
 * Then some performance tests, using runConcurrently()
 * <p>
 * We should run the code in several threads repeatedly calling
 * <p>
 * q.offer(somevalue);
 * q.peek();
 * q.poll();
 * <p>
 * Make sure that the answer is what you would expect
 * <p>
 * Confirm that runConcurrently works by running it with LinkedList
 */
public class HandoverQueueTest {
    // single-threaded correctness tests
    //
    // If the handover queue is broken with one
    // thread, it for sure is broken with many!

    /**
     * Tests that a newly created HandoverQueue.isEmpty() return true.
     */
    @Test
    public void testBasicIsEmpty() {
        fail("todo - please implement");
    }

    /**
     * Test that you can "offer" some elements into the
     * queue and then "poll" them out again.
     */
    @Test
    public void testOfferAndPoll() {
        fail("todo - please implement");
    }

    /**
     * Test that "peek" returns the first element without
     * removing it.
     */
    @Test
    public void testPeek() {
        fail("todo - please implement");
    }

    // multi-threaded bulk random update correctness tests
    //
    // should run the code in several threads, we use runConcurrently() to do that for us
    @Test
    public void testRandomConcurrentUpdates() throws Throwable {
        // your test should fail when you run it with a LinkedList
        // Queue<Integer> q = new LinkedList<>();
        // and it should pass with a well-known concurrent queue
        // Queue<Integer> q = new LinkedBlockingQueue<>();
        HandoverQueue<Integer> q = new HandoverQueue<>();
        long timeToRun = runConcurrently(4, () -> {
            for (int i = 0; i < 1_000_000; i++) {
                // q.offer(somevalue); // check the element was added
                // q.size(); // check the result is within range
                // q.peek(); // check the value is what you would expect
                // q.poll(); // check the value is what you would expect
            }
        });
        System.out.println("timeToRun = " + timeToRun);
        // check that the queue is now empty
        fail("todo - please implement");
    }

    /**
     * DO NOT CHANGE.
     */
    private long runConcurrently(int numberOfThreads, Runnable task) throws Throwable {
        long time = System.currentTimeMillis();
        CyclicBarrier startGun = new CyclicBarrier(numberOfThreads + 1);
        ExecutorService pool = Executors.newFixedThreadPool(numberOfThreads);
        Collection<Future<Void>> futures = new LinkedList<>();
        for (int i = 0; i < numberOfThreads; i++) {
            futures.add(pool.submit(() -> {
                startGun.await();
                task.run();
                return null;
            }));
        }
        startGun.await();
        pool.shutdown();

        for (Future<Void> future : futures) {
            try {
                future.get(1, TimeUnit.MINUTES);
            } catch (ExecutionException e) {
                if (e.getCause() instanceof AssertionError)
                    throw (AssertionError) e.getCause();
                throw e.getCause();
            } catch (TimeoutException e) {
                fail("Job did not complete in time");
            }
        }
        assertTrue("Test did not complete in time",
            pool.awaitTermination(1, TimeUnit.MINUTES));
        time = System.currentTimeMillis() - time;
        return time;
    }
}

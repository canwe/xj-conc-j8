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
        HandoverQueue<Object> handoverQueue = new HandoverQueue();
        Assert.assertTrue(handoverQueue.isEmpty());
    }

    /**
     * Test that you can "offer" some elements into the
     * queue and then "poll" them out again.
     */
    @Test
    public void testOfferAndPoll() {
        HandoverQueue<Object> handoverQueue = new HandoverQueue();
        List<Object> list = Arrays.asList(new Object(), new Object(), new Object());
        for (Object o : list) {
            Assert.assertTrue(handoverQueue.offer(o));
        }
        for (Object o : list) {
            Assert.assertSame(handoverQueue.poll(), o);
        }
    }

    /**
     * Test that "peek" returns the first element without
     * removing it.
     */
    @Test
    public void testPeek() {
        HandoverQueue<Object> handoverQueue = new HandoverQueue();
        Object o = new Object();
        handoverQueue.offer(o);
        Object peek = handoverQueue.peek();
        Assert.assertSame(o, peek);
        Assert.assertFalse(handoverQueue.isEmpty());
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
        Random rand = new Random();
        long timeToRun = runConcurrently(4, () -> {
            for (int i = 0; i < 1_000_000; i++) {
                Integer someValue = rand.nextInt(1_000_000_000);
                Assert.assertTrue(q.offer(someValue)); // check the element was added
                Assert.assertTrue(q.size() > 0); // check the result is within range
                Assert.assertNotNull(q.peek()); // check the value is what you would expect
                Assert.assertNotNull(q.poll()); // check the value is what you would expect
            }
        });
        System.out.println("timeToRun = " + timeToRun);
        // check that the queue is now empty
        Assert.assertTrue(q.isEmpty());
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

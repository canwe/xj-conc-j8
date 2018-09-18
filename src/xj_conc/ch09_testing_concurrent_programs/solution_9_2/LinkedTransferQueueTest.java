package xj_conc.ch09_testing_concurrent_programs.solution_9_2;

import org.junit.*;

import java.util.*;
import java.util.concurrent.*;

public class LinkedTransferQueueTest {
    public static final int REPEATS = 10_000_000;

    @Test(expected = Exception.class)
    public void testLinkedList() throws Exception {
        checkCollection(new LinkedList<>()); // expected to fail
    }

    @Test
    public void testOurLinkedTransferQueue() throws Exception {
        checkCollection(new xj_conc.ch09_testing_concurrent_programs.exercise_9_2.LinkedTransferQueue<>()); // our LTQ from 1.8.0_45
    }

    @Test
    public void testRealLinkedTransferQueue() throws Exception {
        checkCollection(new LinkedTransferQueue<>()); // real LTQ
    }

    @Test
    public void testConcurrentLinkedQueue() throws Exception {
        checkCollection(new ConcurrentLinkedQueue<>()); // should pass
    }

    @Test
    public void testLinkedBlockingQueue() throws Exception {
        checkCollection(new LinkedBlockingQueue<>()); // should pass
    }

    @Test
    public void testConcurrentSkipListSet() throws Exception {
        checkCollection(new ConcurrentSkipListSet<>()); // should pass
    }

    private static void checkCollection(Collection<String> alerts)
        throws InterruptedException, ExecutionException {
        ExecutorService pool = Executors.newCachedThreadPool();
        Future<?> forEachFuture = pool.submit(() -> {
            for (int i = 0; i < REPEATS; i++) {
                // convert the collection to a stream and print each element
                alerts.stream().forEach(System.out::println);
            }
        });
        Future<?> addRemoveFuture = pool.submit(() -> {
            String alert = "fly loose in the server room";
            for (int i = 0; i < REPEATS; i++) {
                // Add and remove again the alert
                alerts.add(alert);
                alerts.remove(alert);
            }
        });
        pool.shutdown();
        forEachFuture.get();
        addRemoveFuture.get();
    }
}

package xj_conc.ch14_memory.solution_14_2_1;

import org.junit.*;

import java.util.*;
import java.util.concurrent.*;

import static org.junit.Assert.*;

public class FailFastCollectionTest {
    private final Collection<String> safe_names =
        new FailFastCollection<>(new ArrayList<>());

    @Before
    public void setUp() throws Exception {
        safe_names.clear();
        Collections.addAll(safe_names, "Anton", "John", "Heinz");
    }

    @Test
    public void testBasicIteration() {
        for (String name : safe_names) {
        }
    }

    @Test
    public void testBasicModification() {
        // we first iterate
        for (String name : safe_names) {
        }
        // then we add Dirk - should work as no client code has a
        // handle to the iterator and we ran the iterator to
        // completion
        safe_names.add("Dirk");
        assertEquals(4, safe_names.size());
        Iterator<String> it = safe_names.iterator();
        assertEquals("Anton", it.next());
        assertEquals("John", it.next());
        assertEquals("Heinz", it.next());
        assertEquals("Dirk", it.next());
    }

    @Test
    public void testModificationFromAnotherThread() throws InterruptedException {
        // this test is more tricky.  Once we have iterated through
        // two elements, we want to add "Neil" to the list.  Since
        // that is done in another thread, we get the exception using
        // futures and check it is a ConcurrentModificationException

        CountDownLatch latch = new CountDownLatch(2);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> future = executor.submit(() -> {
            latch.await();
            safe_names.add("Neil");
            return null;
        });

        for (String name : safe_names) {
            System.out.println(name);
            latch.countDown();
            Thread.sleep(50);
        }

        try {
            future.get();
            fail("expected a ConcurrentModificationException!");
        } catch (ExecutionException e) {
            if (e.getCause() instanceof ConcurrentModificationException) {
                // success
            } else {
                fail("expected a ConcurrentModificationException!");
            }
        }
    }

    @Test
    public void testModificationFromSameThread() {
        // iteration does not run to completion, but iterator is out
        // of scope
        try {
            for (String name : safe_names) {
                if (name.equals("John")) {
                    safe_names.add("Andre");
                }
            }
        } catch (ConcurrentModificationException ex) {
            System.out.println("Expected the error " + ex);
        }

        // now we should be able to use the collection still.
        assertEquals(3, safe_names.size());
        Iterator<String> it = safe_names.iterator();
        assertEquals("Anton", it.next());
        assertEquals("John", it.next());
        assertEquals("Heinz", it.next());

        // we have run to completion, thus the iterator is inactive

        safe_names.add("Andre");

        assertEquals(4, safe_names.size());
        Iterator<String> it2 = safe_names.iterator();
        assertEquals("Anton", it2.next());
        assertEquals("John", it2.next());
        assertEquals("Heinz", it2.next());
        assertEquals("Andre", it2.next());
    }
}

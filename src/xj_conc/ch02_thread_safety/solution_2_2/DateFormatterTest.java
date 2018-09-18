package xj_conc.ch02_thread_safety.solution_2_2;

import net.jcip.annotations.*;
import org.junit.*;
import xj_conc.util.*;

import java.util.*;
import java.util.concurrent.*;

import static org.junit.Assert.*;

/**
 * DO NOT CHANGE.
 */
public class DateFormatterTest {
    public static final int THREADS = 4;
    public static final int UPTO = 1_000_000;

    @Test
    public void testConcurrentAccess() throws Exception {
        SuperSimpleGCParser.showGCLogSummaryAtExit();
        long time = System.currentTimeMillis();
        DateFormatter df = new DateFormatter();
        ExecutorService pool = Executors.newCachedThreadPool();
        Collection<Future<String>> futures = new ArrayList<>();
        for (int i = 0; i < THREADS; i++) {
            futures.add(pool.submit(() ->
            {
                String now = "2015-02-09";
                for (int j = 0; j < UPTO; j++) {
                    String parseFormat =
                        df.format(df.parse(now));
                    assertEquals(now, parseFormat);
                }
                return "OK";
            }));
        }
        for (Future<String> future : futures) {
            assertEquals("OK", future.get());
        }
        time = System.currentTimeMillis() - time;
        pool.shutdown();
        assertTrue("pool did not shut down in the expected time", pool.awaitTermination(100, TimeUnit.MILLISECONDS));
        System.out.println("time = " + time);
    }

    @Test
    public void testClassStructure() throws InterruptedException {
        assertEquals("DateFormatter should not have any fields",
            0, DateFormatter.class.getDeclaredFields().length);
    }

    @Test
    public void testAnnotation() throws InterruptedException {
        assertNotNull("Class should be annotated as @ThreadSafe",
            DateFormatter.class.getAnnotation(ThreadSafe.class));
    }
}

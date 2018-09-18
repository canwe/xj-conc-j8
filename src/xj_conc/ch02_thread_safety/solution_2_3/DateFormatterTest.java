package xj_conc.ch02_thread_safety.solution_2_3;

import net.jcip.annotations.*;
import org.junit.*;
import xj_conc.util.*;

import java.lang.reflect.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.Assert.*;
import static xj_conc.util.TestHelpers.*;

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
        boolean found = false;
        for (Field field : DateFormatter.class.getDeclaredFields()) {
            if (field.getType() == DateFormat.class) {
                found = true;
                if (Modifier.isStatic(field.getModifiers())) {
                    fail("DateFormat df field should not be static");
                }
                if (!Modifier.isPrivate(field.getModifiers())) {
                    fail("DateFormat df field should be private (encapsulation");
                }
                if (!Modifier.isFinal(field.getModifiers())) {
                    fail("DateFormat df field should be final (encapsulation");
                }
            }
        }
        assertTrue("Could not find DateFormat field", found);

        for (Method method : DateFormatter.class.getDeclaredMethods()) {
            if (!Modifier.isSynchronized(method.getModifiers())) {
                fail("Method " + method.getName() + " is not sychronized");
            }
        }
    }

    @Test
    public void testAnnotation() throws InterruptedException {
        assertTypeIsAnnotated(ThreadSafe.class, DateFormatter.class);
        boolean found = false;
        for (Field field : DateFormatter.class.getDeclaredFields()) {
            if (field.getType() == DateFormat.class) {
                found = true;
                assertNotNull("Field should be annotated as @GuardedBy",
                    field.getAnnotation(GuardedBy.class));
            }
        }
        assertTrue("Could not find DateFormat field", found);
    }
}

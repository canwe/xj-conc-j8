package xj_conc.ch04_task_execution.solution_4_1b;

import org.junit.*;

import java.lang.management.*;

import static junit.framework.Assert.*;
import static xj_conc.ch04_task_execution.exercise_4_1.Constants.*;

public class ParallelFactorizerTest {
    /**
     * DO NOT CHANGE.
     */
    @Test
    public void testParallelFactorizing() throws InterruptedException {
        ThreadMXBean tb = ManagementFactory.getThreadMXBean();
        long totalTime = System.currentTimeMillis();
        int threads = tb.getPeakThreadCount();
        ParallelFactorizer pf = new ParallelFactorizer();
        int primes = pf.factorizeInParallel(START, NUMBERS_TO_CHECK);
        System.out.println("primes = " + primes);
        assertEquals(1514, primes);
        threads = tb.getPeakThreadCount() - threads;
        System.out.println("threads = " + threads);
        totalTime = System.currentTimeMillis() - totalTime;
        System.out.println("totalTime = " + totalTime);
        assertTrue("Too many threads created: " + threads,
            threads < Runtime.getRuntime().availableProcessors() * 2 + 5);
    }
}

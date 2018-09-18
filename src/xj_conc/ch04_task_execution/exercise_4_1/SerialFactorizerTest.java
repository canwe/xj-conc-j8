package xj_conc.ch04_task_execution.exercise_4_1;

import org.junit.*;
import xj_conc.math.*;

import java.lang.management.*;
import java.util.stream.*;

import static junit.framework.Assert.*;
import static xj_conc.ch04_task_execution.exercise_4_1.Constants.*;

/**
 * DO NOT CHANGE.
 */
public class SerialFactorizerTest {
    @Test
    public void testSerialFactorizing() throws InterruptedException {
        long totalTime = System.currentTimeMillis();
        long primes = LongStream.range(START, START + NUMBERS_TO_CHECK).
            filter(number -> Factorizer.factor(number).length == 1).
            count();
        System.out.println("primes = " + primes);
        assertEquals(1514, primes);
        ThreadMXBean tb = ManagementFactory.getThreadMXBean();
        System.out.println("PeakThreadCount = " + tb.getPeakThreadCount());
        totalTime = System.currentTimeMillis() - totalTime;
        System.out.println("totalTime = " + totalTime);
    }
}

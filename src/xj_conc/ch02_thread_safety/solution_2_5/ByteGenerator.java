package xj_conc.ch02_thread_safety.solution_2_5;

import java.util.concurrent.atomic.*;

/**
 * The simplest solution is to simply cast the int to a byte.  After all, the
 * range of the values are from Byte.MIN_VALUE until Byte.MAX_VALUE.  Casting
 * the int to a byte will only show the lowest 8 bits, which will have the same
 * effect as that complicated code by Neeme Praks.
 */
public class ByteGenerator {
    private static final int INITIAL_VALUE = Byte.MIN_VALUE - 1;
    private final AtomicInteger counter = new AtomicInteger(INITIAL_VALUE);

    public byte nextValue() {
        return (byte) counter.incrementAndGet();
    }
}

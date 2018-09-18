package xj_conc.ch02_thread_safety.exercise_2_5;

import java.util.concurrent.atomic.*;

/**
 * TODO: Figure out why we have a race condition and then fix it.
 * Code by Neeme Praks, used with permission.
 * http://stackoverflow.com/questions/7384875/what-is-wrong-with-this-thread-safe-byte-sequence-generator/7387782#7387782
 *
 * @author Neeme Praks
 */
public class ByteGenerator {
    private static final int INITIAL_VALUE = Byte.MIN_VALUE - 1;

    private final AtomicInteger counter = new AtomicInteger(INITIAL_VALUE);
    private final AtomicInteger resetCounter = new AtomicInteger(0);

    private final Object lock = new Object();

    public byte nextValue() {
        //int next = counter.incrementAndGet();
        //if (next > Byte.MAX_VALUE) {
        //    synchronized (lock) {
        //        int i = counter.get(); //might be a new value
        //        // if value is still larger than max byte value, we reset it
        //        if (i > Byte.MAX_VALUE) {
        //            counter.set(INITIAL_VALUE);
        //            resetCounter.incrementAndGet();
        //        }
        //        next = counter.incrementAndGet();
        //    }
        //}
        return (byte) counter.incrementAndGet();

    }
}

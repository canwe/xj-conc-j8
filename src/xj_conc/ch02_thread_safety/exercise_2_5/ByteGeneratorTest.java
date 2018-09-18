package xj_conc.ch02_thread_safety.exercise_2_5;

import org.junit.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

import static junit.framework.Assert.*;

/**
 * DO NOT CHANGE.
 */
public class ByteGeneratorTest {
    @Test
    public void testGenerate() {
        ByteGenerator g = new ByteGenerator();
        IntStream.range(0, 10).forEach(
            n -> IntStream.rangeClosed(Byte.MIN_VALUE, Byte.MAX_VALUE).
                forEach(i -> assertEquals(i, g.nextValue())));
    }


    private static final int REPEATS = 10_000;

    @Test
    public void testGenerateMultiThreaded() throws InterruptedException {
        ByteGenerator g = new ByteGenerator();
        AtomicInteger[] counters =
            Stream.generate(() -> new AtomicInteger(0)).
                limit(Byte.MAX_VALUE - Byte.MIN_VALUE + 1).
                toArray(AtomicInteger[]::new);
        int nThreads = 100;
        ExecutorService pool = Executors.newFixedThreadPool(nThreads);
        IntStream.range(0, nThreads).
            forEach(thread -> pool.submit(
                () -> IntStream.range(0, REPEATS).
                    forEach(
                        n -> IntStream.rangeClosed(Byte.MIN_VALUE, Byte.MAX_VALUE).
                            forEach(i -> {
                                byte value = g.nextValue();
                                counters[value - Byte.MIN_VALUE].incrementAndGet();
                            })
                    )
            ));

        pool.shutdown();
        while (!pool.awaitTermination(1, TimeUnit.SECONDS)) ;


        int[] i = {0};
        Stream.of(counters).forEach(
            counter -> System.out.printf("value #%d: %d%n", (i[0]++ + Byte.MIN_VALUE), counter.get())
        );
        i[0] = 0;
        //print out the number of hits for each value
        Stream.of(counters).forEach(
            counter ->
                assertEquals("value #" + (i[0]++ + Byte.MIN_VALUE),
                    nThreads * REPEATS, counter.get()));
    }
}

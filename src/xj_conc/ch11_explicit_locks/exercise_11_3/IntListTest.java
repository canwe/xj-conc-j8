package xj_conc.ch11_explicit_locks.exercise_11_3;

import sun.misc.*;
import xj_conc.util.*;

import java.lang.management.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.*;

public class IntListTest {
    private static final ThreadMXBean tmbean = ManagementFactory.getThreadMXBean();

    private static final Unsafe UNSAFE;
    private static final long SIZE_OFFSET;
    private static final long ELEMENT_OFFSET;
    private static final long ADD_OFFSET;
    private static final long REMOVE_OFFSET;
    public static final int REPEATS = 10;

    static {
        VMArgsChecker.findVMArg("-XX:-RestrictContended").orElseThrow(
            () -> new AssertionError("Please use -XX:-RestrictContended JVM flag"));

        try {
            Field theUnsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafeField.setAccessible(true);
            UNSAFE = (Unsafe) theUnsafeField.get(null);
            SIZE_OFFSET = UNSAFE.objectFieldOffset(IntListTest.class.getDeclaredField("sizeTest"));
            ELEMENT_OFFSET = UNSAFE.objectFieldOffset(IntListTest.class.getDeclaredField("elementTest"));
            ADD_OFFSET = UNSAFE.objectFieldOffset(IntListTest.class.getDeclaredField("addTest"));
            REMOVE_OFFSET = UNSAFE.objectFieldOffset(IntListTest.class.getDeclaredField("removeTest"));
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Contended
    private int sizeTest = 0;
    @Contended
    private int elementTest = 0;
    @Contended
    private int addTest = 0;
    @Contended
    private int removeTest = 0;

    private static LongAccumulator bestSizeThread = new LongAccumulator(Long::max, 0);
    private static LongAccumulator bestGetThread = new LongAccumulator(Long::max, 0);
    private static LongAccumulator bestAddRemoveThread = new LongAccumulator(Long::max, 0);
    private static LongAccumulator worstSizeThread = new LongAccumulator(Long::min, Long.MAX_VALUE);
    private static LongAccumulator worstGetThread = new LongAccumulator(Long::min, Long.MAX_VALUE);
    private static LongAccumulator worstAddRemoveThread = new LongAccumulator(Long::min, Long.MAX_VALUE);

    public static void main(String... args) throws InterruptedException {
        System.out.println("c/e = cpu time / elapsed time");
        System.out.println("s/e = system cpu time / elapsed time");
        System.out.println("u/e = user cpu time / elapsed time");
        for (int i = 0; i < REPEATS; i++) {
            new IntListTest().test();
        }

        System.out.println();
        System.out.println("Best values:");
        System.out.printf(Locale.US, "\tsize()        %,d%n", bestSizeThread.longValue());
        System.out.printf(Locale.US, "\tget()         %,d%n", bestGetThread.longValue());
        System.out.printf(Locale.US, "\tadd/remove()  %,d%n", bestAddRemoveThread.longValue());
        System.out.println("Worst values:");
        System.out.printf(Locale.US, "\tsize()        %,d%n", worstSizeThread.longValue());
        System.out.printf(Locale.US, "\tget()         %,d%n", worstGetThread.longValue());
        System.out.printf(Locale.US, "\tadd/remove()  %,d%n", worstAddRemoveThread.longValue());
    }

    private void test() throws InterruptedException {
        IntList list = new IntList();
        AtomicBoolean testing = new AtomicBoolean(true);
        Thread[] threads = {
            new Thread("sizeThread") {
                public void run() {
                    long time = System.currentTimeMillis();
                    long userTime = tmbean.getCurrentThreadUserTime();
                    long cpuTime = tmbean.getCurrentThreadCpuTime();
                    long count = 0;
                    while (testing.get()) {
                        int size = list.size();
                        UNSAFE.putOrderedInt(IntListTest.this, SIZE_OFFSET, size);
                        count++;
                    }
                    bestSizeThread.accumulate(count);
                    worstSizeThread.accumulate(count);
                    time = System.currentTimeMillis() - time;
                    userTime = tmbean.getCurrentThreadUserTime() - userTime;
                    cpuTime = tmbean.getCurrentThreadCpuTime() - cpuTime;
                    System.out.printf(Locale.US, "size() called %,d times, c/e=%d%%, s/e=%d%%, u/e=%d%%%n",
                        count, (cpuTime / time) / 10_000, (cpuTime - userTime) / time / 10_000, userTime / time / 10_000);
                }
            },
            new Thread("getThread") {
                public void run() {
                    long time = System.currentTimeMillis();
                    long userTime = tmbean.getCurrentThreadUserTime();
                    long cpuTime = tmbean.getCurrentThreadCpuTime();
                    long count = 0;
                    while (list.size() < 50) ; // wait
                    while (testing.get()) {
                        int element = list.get(10);
                        UNSAFE.putOrderedInt(IntListTest.this, ELEMENT_OFFSET, element);
                        count++;
                    }
                    bestGetThread.accumulate(count);
                    worstGetThread.accumulate(count);
                    time = System.currentTimeMillis() - time;
                    userTime = tmbean.getCurrentThreadUserTime() - userTime;
                    cpuTime = tmbean.getCurrentThreadCpuTime() - cpuTime;
                    System.out.printf(Locale.US, "get() called %,d times, c/e=%d%%, s/e=%d%%, u/e=%d%%%n",
                        count, (cpuTime / time) / 10_000, (cpuTime - userTime) / time / 10_000, userTime / time / 10_000);
                }
            },
            new Thread("add/removeThreads") {
                public void run() {
                    long time = System.currentTimeMillis();
                    long userTime = tmbean.getCurrentThreadUserTime();
                    long cpuTime = tmbean.getCurrentThreadCpuTime();
                    long count = 0;
                    for (int i = 1; i < 50; i++) {
                        boolean add = list.add(i);
                        UNSAFE.putOrderedInt(IntListTest.this, ADD_OFFSET, add ? 1 : 0);
                        count++;
                    }

                    while (testing.get()) {
                        for (int i = 1; i < 500; i++) {
                            boolean add = list.add(i);
                            UNSAFE.putOrderedInt(IntListTest.this, ADD_OFFSET, add ? 1 : 0);
                            count++;
                        }
                        for (int i = 1; i < 500; i++) {
                            int remove = list.remove(7);
                            UNSAFE.putOrderedInt(IntListTest.this, REMOVE_OFFSET, remove);
                            count++;
                        }
                    }
                    bestAddRemoveThread.accumulate(count);
                    worstAddRemoveThread.accumulate(count);
                    time = System.currentTimeMillis() - time;
                    userTime = tmbean.getCurrentThreadUserTime() - userTime;
                    cpuTime = tmbean.getCurrentThreadCpuTime() - cpuTime;
                    System.out.printf(Locale.US, "add()/remove() called %,d times, c/e=%d%%, s/e=%d%%, u/e=%d%%%n",
                        count, (cpuTime / time) / 10_000, (cpuTime - userTime) / time / 10_000, userTime / time / 10_000);
                }
            },
            new Thread("trimToSize") {
                public void run() {
                    while (testing.get()) {
                        list.trimToSize();
                        try {
                            Thread.sleep(5);
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                }
            }
        };
        for (Thread thread : threads) {
            thread.start();
        }

        Thread.sleep(3000);
        testing.set(false);
        for (Thread thread : threads) {
            thread.interrupt();
            thread.join();
        }
    }
}


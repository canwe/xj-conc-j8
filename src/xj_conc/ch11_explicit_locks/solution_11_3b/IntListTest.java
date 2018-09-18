package xj_conc.ch11_explicit_locks.solution_11_3b;

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
            System.out.println("SIZE_OFFSET = " + SIZE_OFFSET);
            System.out.println("ELEMENT_OFFSET = " + ELEMENT_OFFSET);
            System.out.println("ADD_OFFSET = " + ADD_OFFSET);
            System.out.println("REMOVE_OFFSET = " + REMOVE_OFFSET);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Contended
    private volatile int sizeTest = 0;
    @Contended
    private volatile int elementTest = 0;
    @Contended
    private volatile int addTest = 0;
    @Contended
    private volatile int removeTest = 0;

    public static void main(String... args) throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            new IntListTest().test();
        }
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
                    time = System.currentTimeMillis() - time;
                    userTime = tmbean.getCurrentThreadUserTime() - userTime;
                    cpuTime = tmbean.getCurrentThreadCpuTime() - cpuTime;
                    System.out.printf(Locale.US, "add()/remove() called %,d times, c/e=%d%%, s/e=%d%%, u/e=%d%%%n",
                        count, (cpuTime / time) / 10_000, (cpuTime - userTime) / time / 10_000, userTime / time / 10_000);
                }
            }
        };
        for (Thread thread : threads) {
            thread.start();
        }

        Thread.sleep(3000);
        testing.set(false);
        for (Thread thread : threads) {
            thread.join();
        }
    }
}


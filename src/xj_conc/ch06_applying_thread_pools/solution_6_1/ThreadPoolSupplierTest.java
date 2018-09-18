package xj_conc.ch06_applying_thread_pools.solution_6_1;

import org.junit.*;

import java.util.concurrent.*;

import static org.junit.Assert.*;

public class ThreadPoolSupplierTest {
    private static final ThreadPoolSupplier tps = new ThreadPoolSupplier();

    @Test
    public void testQueue() {
        ThreadPoolExecutor tpe = tps.apply("testQueue");
        try {
            assertEquals("Queue is not bounded to 1000", 1000, tpe.getQueue().remainingCapacity());
            assertEquals("Queue is not a LinkedBlockingQueue", LinkedBlockingQueue.class, tpe.getQueue().getClass());
        } finally {
            tpe.shutdown();
        }
    }

    @Test
    public void testSizes() {
        ThreadPoolExecutor tpe = tps.apply("testSizes");
        try {
            assertEquals("Incorrect core pool size", 10, tpe.getCorePoolSize());
            assertEquals("Incorrect maximum pool size", 100, tpe.getMaximumPoolSize());
        } finally {
            tpe.shutdown();
        }
    }

    @Test
    public void testPriorityAndNonDaemon() throws Throwable {
        BlockingQueue<Object> result = new LinkedBlockingQueue<>();
        Thread daemonCreator = new Thread() {
            public void run() {
                ThreadPoolExecutor tpe = tps.apply("testPriorityAndNonDaemon");
                try {
                    Future<?> future = tpe.submit(() ->
                    {
                        if (Thread.currentThread().getPriority() != Thread.MAX_PRIORITY - 2) {
                            throw new IllegalArgumentException("Incorrect priority for thread in pool");
                        }
                        if (Thread.currentThread().isDaemon()) {
                            throw new IllegalArgumentException("Incorrect daemon status in pool");
                        }
                    });
                    try {
                        future.get();
                        result.add(true);
                    } catch (ExecutionException | InterruptedException e) {
                        result.add(e.getCause());
                    }
                } finally {
                    tpe.shutdown();
                }
            }
        };
        daemonCreator.setDaemon(true);
        daemonCreator.start();
        daemonCreator.join(); // wait to finish
        Object o = result.poll(1, TimeUnit.SECONDS);
        if (o == null) {
            fail("Timeout");
        } else if (o instanceof Throwable) {
            fail("Error - " + o);
        } else if (Boolean.TRUE.equals(o)) {
            System.out.println("All good");
        } else {
            fail("Unknown result - " + o);
        }
    }

    @Test(expected = RejectedExecutionException.class)
    public void testPoolShutdown() {
        ThreadPoolExecutor tpe = tps.apply("testPoolShutdown");
        tpe.shutdown();
        tpe.submit(() -> System.out.println("This should not work"));
    }

    @Test
    public void testCallerRunsExceptShutdown() throws Throwable {
        Thread mainThread = Thread.currentThread();
        ThreadPoolExecutor tpe = tps.apply("testCallerRunsExceptShutdown");
        Phaser phaser = new Phaser(1101);
        for (int i = 0; i < 1100; i++) {
            tpe.submit(phaser::arriveAndAwaitAdvance);
        }
        Future<?> future = tpe.submit(() -> {
            assertEquals(mainThread, Thread.currentThread());
            for (int i = 0; i < 1001; i++) {
                phaser.arriveAndDeregister();
            }
        });
        try {
            future.get();
        } catch (ExecutionException e) {
            throw e.getCause();
        }

        tpe.shutdown();
        assertTrue(tpe.awaitTermination(1, TimeUnit.SECONDS));
        System.out.println("All good");
    }

    @Test
    public void testFiveSecondTimeout() throws Throwable {
        ThreadPoolExecutor tpe = tps.apply("testFiveSecondTimeout");
        Phaser phaser = new Phaser(1101);
        for (int i = 0; i < 1100; i++) {
            tpe.submit(phaser::arriveAndAwaitAdvance);
        }
        for (int i = 0; i < 1001; i++) {
            phaser.arriveAndDeregister();
        }

        for (int i = 0; i < 4; i++) {
            Thread.sleep(1000);
            assertEquals(100, tpe.getPoolSize());
            System.out.println("getPoolSize() = " + tpe.getPoolSize());
        }

        Thread.sleep(1500);
        assertEquals(10, tpe.getPoolSize());
        System.out.println("getPoolSize() = " + tpe.getPoolSize());

        tpe.shutdown();
        assertTrue(tpe.awaitTermination(1, TimeUnit.SECONDS));
        System.out.println("All good");
    }

    @Test
    public void testThreadNames() throws Throwable {
        ThreadPoolExecutor tpe1 = tps.apply("testThreadNames1");
        ThreadPoolExecutor tpe2 = tps.apply("testThreadNames2");
        String name1_1 = tpe1.submit(() -> Thread.currentThread().getName()).get();
        String name1_2 = tpe1.submit(() -> Thread.currentThread().getName()).get();
        String name2_1 = tpe2.submit(() -> Thread.currentThread().getName()).get();
        String name2_2 = tpe2.submit(() -> Thread.currentThread().getName()).get();
        tpe1.shutdown();
        tpe2.shutdown();

        assertTrue(tpe1.awaitTermination(1, TimeUnit.SECONDS));
        assertTrue(tpe2.awaitTermination(1, TimeUnit.SECONDS));

        assertEquals("Threads within one pool should have the same prefix", prefix(name1_1), prefix(name1_2));
        assertEquals("Threads within one pool should have the same prefix", prefix(name2_1), prefix(name2_2));
        assertFalse("Threads within two different pools should not have the same prefix", prefix(name1_1).equals(prefix(name2_1)));
        assertTrue(name1_1.matches(".*#testThreadNames1$"));
        assertTrue(name1_2.matches(".*#testThreadNames1$"));
        assertTrue(name2_1.matches(".*#testThreadNames2$"));
        assertTrue(name2_2.matches(".*#testThreadNames2$"));
        System.out.println("All good");
    }

    private String prefix(String name) {
        return name.substring(0, name.lastIndexOf('-'));
    }
}


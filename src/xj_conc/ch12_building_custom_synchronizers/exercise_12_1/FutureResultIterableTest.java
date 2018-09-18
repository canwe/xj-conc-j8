package xj_conc.ch12_building_custom_synchronizers.exercise_12_1;


import org.junit.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import static org.junit.Assert.*;

/**
 * DO NOT CHANGE.
 */
public class FutureResultIterableTest {
    @Test
    public void testWithNoSubmits() throws InterruptedException {
        FutureResultIterable<String> fri = new FutureResultIterable<>();
        fri.shutdown();
        Thread.sleep(100);
        assertTrue(fri.isTerminated());
    }

    @Test
    public void testWithPauseAfterSubmits() throws Throwable {
        FutureResultIterable<Integer> fri = new FutureResultIterable<>();
        for (int i = 0; i < 10; i++) {
            fri.submit(() -> ThreadLocalRandom.current().nextInt());
        }
        Thread.sleep(100);
        for (int i = 0; i < 10; i++) {
            fri.submit(() -> ThreadLocalRandom.current().nextInt());
        }

        ExecutorService oneShot = Executors.newSingleThreadExecutor();
        Future<Integer> totalFuture = oneShot.submit(() -> {
                int total = 0;
                Iterator<Future<Integer>> it = fri.iterator();
                for (int i = 0; i < 20; i++) {
                    int val = it.next().get();
                    total += val;
                    System.out.println(val);
                }
                return total;
            }
        );

        int total = totalFuture.get(100, TimeUnit.MILLISECONDS);
        System.out.println("total = " + total);
        oneShot.shutdown();
        fri.shutdown();
        Thread.sleep(100);
        assertTrue(fri.isTerminated());
    }

    @Test
    public void testWithOneSubmit() throws ExecutionException, InterruptedException {
        FutureResultIterable<String> fri = new FutureResultIterable<>();
        fri.submit(() -> {
            System.out.println("Trying call");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "Hello World";
        });

        Iterator<Future<String>> iterator = fri.iterator();
        assertTrue(iterator.hasNext());
        String str = iterator.next().get();
        System.out.println(str);
        assertEquals("Hello World", str);
        assertFalse(iterator.hasNext());
        fri.shutdown();
        Thread.sleep(500);
        assertTrue(fri.isTerminated());

        // after shutdown we can get the iterator again and it will return the
        // results as before
        iterator = fri.iterator();
        assertTrue(iterator.hasNext());
        str = iterator.next().get();
        System.out.println(str);
        assertEquals("Hello World", str);
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testWithTenSubmits() throws ExecutionException, InterruptedException {
        FutureResultIterable<String> fri = new FutureResultIterable<>();
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            fri.submit(() -> {
                try {
                    Thread.sleep((long) (500 + Math.random() * 300));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return "Hello World " + finalI;
            });
        }

        long time = System.currentTimeMillis();
        int count = 0;
        for (Future<String> future : fri) {
            System.out.println(future.get());
            count++;
        }
        assertEquals(10, count);
        time = System.currentTimeMillis() - time;
        assertTrue(time < 1000);
        fri.shutdown();
    }

    @Test(expected = RejectedExecutionException.class)
    public void testSubmitAfterShutdown() {
        FutureResultIterable<String> fri = new FutureResultIterable<>();
        fri.shutdown();
        fri.submit(() -> {
            System.out.println("Trying call");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "Hello World";
        });
    }

    @Test
    public void testWithEarlyShutdown() throws ExecutionException, InterruptedException {
        FutureResultIterable<String> fri = new FutureResultIterable<>();
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            fri.submit(() -> {
                try {
                    Thread.sleep((long) (500 + Math.random() * 300));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return "Hello World " + finalI;
            });
        }

        fri.shutdown();

        long time = System.currentTimeMillis();
        int count = 0;
        for (Future<String> future : fri) {
            System.out.println(future.get());
            count++;
        }
        assertEquals(10, count);
        time = System.currentTimeMillis() - time;
        assertTrue(time < 1000);
    }

    @Test
    public void testIndexOutOfBoundsProblem() {
        FutureResultIterable<String> fri = new FutureResultIterable<>();
        Thread.currentThread().interrupt();
        fri.submit(() -> {
            System.out.println("Trying call");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "Hello World";
        });
        Thread.currentThread().interrupt();
        fri.iterator().next();
        assertTrue(Thread.interrupted());
        fri.shutdown();
    }

    @Test
    public void testConcurrentExecuteAndIterate() throws InterruptedException {
        FutureResultIterable<Integer> fri =
            new FutureResultIterable<>();
        fri.submit(() -> {
            Thread.sleep(100);
            return 0;
        });
        Thread t = new Thread() {
            public void run() {
                for (Future<Integer> future : fri) {
                    try {
                        System.out.println(future.get());
                    } catch (InterruptedException | ExecutionException e) {
                        System.err.println(e);
                    }
                }
            }
        };
        t.start();
        for (int i = 1; i < 10; i++) {
            int finalI = i;
            fri.submit(() -> {
                Thread.sleep(100 * (finalI + 1));
                return finalI;
            });
        }
        long time = System.currentTimeMillis();
        t.join();
        time = System.currentTimeMillis() - time;
        assertTrue("threads do not get updated with new futures",
            time > 900);
        fri.shutdown();
    }

    @Test
    public void testMultipleIterators() throws ExecutionException, InterruptedException {
        FutureResultIterable<Integer> fri = new FutureResultIterable<>();
        for (int i = 1; i <= 10; i++) {
            int finalI = i;
            fri.submit(() -> finalI);
        }
        int total = 0;
        for (Future<Integer> future : fri) {
            total += future.get();
        }
        assertEquals(55, total);

        total = 0;
        for (Future<Integer> future : fri) {
            total += future.get();
        }
        assertEquals(55, total);

        total = 0;
        for (Future<Integer> future : fri) {
            total += future.get();
        }
        assertEquals(55, total);
        fri.shutdown();
    }

    @Test
    public void testForLivelock() throws InterruptedException {
        FutureResultIterable<String> fri = new FutureResultIterable<>();
        fri.submit(() -> {
            Thread.sleep(100);
            System.out.println("Job done in testForLivelock()");
            return "All good";
        });
        fri.shutdown();
        Thread iterator = new Thread() {
            public void run() {
                for (Future<String> stringFuture : fri) {
                    try {
                        System.out.println(stringFuture.get());
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        iterator.start();
        Thread.sleep(30);
        iterator.interrupt();
        iterator.join();
        assertTrue(!iterator.isAlive());
    }


    @Test(expected = NoSuchElementException.class)
    public void testForNoSuchElementException() throws InterruptedException {
        FutureResultIterable<String> fri = new FutureResultIterable<>();
        Iterator<Future<String>> iterator = fri.iterator();
        assertFalse(iterator.hasNext());
        iterator.next();
        fri.shutdown();
    }

    @Test
    public void testMultipleThreadsWaiting() throws InterruptedException, ExecutionException, TimeoutException {
        FutureResultIterable<Integer> fri = new FutureResultIterable<>();
        for (int i = 1; i <= 10; i++) {
            int value = i;
            fri.submit(() -> {
                Thread.sleep(value * 100);
                return value;
            });
        }

        Thread.sleep(400);
        List<Future<Integer>> addResult = new ArrayList<>();
        ExecutorService cached = Executors.newCachedThreadPool();

        for (int i = 0; i < 10; i++) {
            addResult.add(cached.submit(() -> {
                int total = 0;
                for (Future<Integer> future : fri) {
                    total += future.get(2, TimeUnit.SECONDS);
                }
                return total;
            }));
        }
        for (Future<Integer> future : addResult) {
            assertEquals(55, future.get(2, TimeUnit.SECONDS).intValue());
        }
    }

    @Test
    public void testLastTaskSlowHoldingUpOtherIterator() throws InterruptedException {
        FutureResultIterable<Integer> fri = new FutureResultIterable<>();
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            fri.submit(() -> {
                Thread.sleep((1 + finalI) * 100);
                return finalI;
            });
        }
        fri.submit(() -> {
            Thread.sleep(2_000);
            return 100;
        });
        CountDownLatch go = new CountDownLatch(1);
        Thread thread1 = new Thread() {
            public void run() {
                for (Future<Integer> val : fri) {
                    try {
                        Integer number = val.get();
                        System.out.println("number = " + number);
                        if (number == 9) {
                            go.countDown();
                        }
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                }
            }
        };
        AtomicBoolean success = new AtomicBoolean(false);
        Thread thread2 = new Thread() {
            public void run() {
                try {
                    go.await();
                    System.out.println("Starting reading from thread2");
                    long time = System.currentTimeMillis();
                    for (Future<Integer> val : fri) {
                        if (val.get(50, TimeUnit.MILLISECONDS) == 9) {
                            time = System.currentTimeMillis() - time;
                            success.set(time < 50);
                            return;
                        }
                    }
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
        };
        thread1.start();
        thread2.start();
        thread2.join();
        assertTrue("thread2 did not complete the first 10 reads fast enough", success.get());
    }
}

package xj_conc.ch11_explicit_locks.solution_11_2b;

import xj_conc.ch08_avoiding_liveness_hazards.exercise_8_1.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;

/**
 * Our philosopher always first locks left, then right.  If all of the thinkers
 * sit in a circle and their threads call "drink()" at the same time, then they
 * will end up with a deadlock.
 * <p>
 * In our solution we did not add a random sleep, but you might need to do this
 * if you have a lot of conflicts.
 * <p>
 * A fun thing to try is to count how many times you had to back off when you
 * could not acquire a lock.
 *
 * @author Heinz Kabutz
 */
public class Thinker implements Callable<ThinkerStatus> {
    private final int id;
    private final Krasi left, right;
    private final AtomicLong retry = new AtomicLong();
    private int drinks;

    public Thinker(int id, Krasi left, Krasi right) {
        this.id = id;
        this.left = left;
        this.right = right;
    }

    @Override
    public ThinkerStatus call() throws Exception {
        for (int i = 0; i < 1000; i++) {
            drink();
            think();
        }
        return drinks == 1000 ? ThinkerStatus.HAPPY_THINKER :
            ThinkerStatus.UNHAPPY_THINKER;
    }

    @SuppressWarnings("boxing")
    public void drink() {
        while (true) {
            left.lock();
            try {
                if (right.tryLock()) {
                    try {
                        drinking();
                        return; // remember to return after a good drink
                    } finally {
                        right.unlock();
                    }
                }
            } finally {
                left.unlock();
            }
            // Possibly add a short random sleep to avoid a livelock, but only
            // do this after you have unlocked both locks.
            retry.incrementAndGet();
            LockSupport.parkNanos(System.nanoTime() & 0xFFFF);
        }
    }

    private void drinking() {
        if (!left.isHeldByCurrentThread() || !right.isHeldByCurrentThread()) {
            throw new IllegalMonitorStateException("Not holding both locks");
        }
        System.out.printf("(%d) Drinking%n", id);
        drinks++;
    }

    public int getRetry() {
        return retry.intValue();
    }

    @SuppressWarnings("boxing")
    public void think() {
        System.out.printf("(%d) Thinking%n", id);
    }
}

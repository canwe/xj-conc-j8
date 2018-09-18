package xj_conc.ch11_explicit_locks.solution_11_2;

import xj_conc.ch08_avoiding_liveness_hazards.exercise_8_1.*;

import java.util.concurrent.*;
import java.util.concurrent.locks.*;

/**
 * Our philosopher always first locks left, then right.  If all of the thinkers
 * sit in a circle and their threads call "drink()" at the same time, then they
 * will end up with a deadlock.
 * <p>
 * Instead of calling lock() on the two Krasi instances, we want to use
 * tryLock().  The idea is something like:
 * <p>
 * <pre>
 *     while(true) {
 *         try lock left
 *         if successful, try lock right
 *           if successful drink and unlock right
 *           unlock left
 *     }
 * </pre>
 * <p>
 * Be careful to not cause a livelock.
 *
 * @author Heinz Kabutz
 */
public class Thinker implements Callable<ThinkerStatus> {
    private final int id;
    private final Krasi right, left;
    private int drinks;

    public Thinker(int id, Krasi right, Krasi left) {
        this.id = id;
        this.right = right;
        this.left = left;
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
            right.lock();
            try {
                if (left.tryLock()) {
                    try {
                        drinking();
                        return;
                    } finally {
                        left.unlock();
                    }
                }
            } finally {
                right.unlock();
            }
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

    @SuppressWarnings("boxing")
    public void think() {
        System.out.printf("(%d) Thinking%n", id);
    }
}

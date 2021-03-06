package xj_conc.ch08_avoiding_liveness_hazards.exercise_8_1;

import java.util.concurrent.*;

/**
 * Our philosopher always first locks left, then right.  If all of the thinkers
 * sit in a circle and their threads call "drink()" at the same time, then they
 * will end up with a deadlock.
 * <p>
 * Instead of locking first on left and then on right, change the code to lock
 * on the bigger Krasi first.  This will avoid the deadlock, as we will always
 * lock in the same order.
 *
 * @author Heinz Kabutz
 */
public class Thinker implements Callable<ThinkerStatus> {
    private final int id;
    private final Krasi right, left;
    private int drinks = 0;

    public Thinker(int id, Krasi right, Krasi left) {
        this.id = id;

        boolean order = left.compareTo(right) > 0;
        this.left = order ? left : right;
        this.right = order ? right : left;
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
        synchronized (right) {
            synchronized (left) {
                drinking();
            }
        }
    }

    private void drinking() {
        if (!Thread.holdsLock(left) || !Thread.holdsLock(right)) {
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

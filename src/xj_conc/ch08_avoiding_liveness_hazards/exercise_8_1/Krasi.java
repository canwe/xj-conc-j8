package xj_conc.ch08_avoiding_liveness_hazards.exercise_8_1;

import java.util.concurrent.atomic.AtomicLong;

/**
 * "Krasi" means wine in Greek, which is where the philosophers used to live
 * many thousands of years ago.  We use the Krasi objects as locks to
 * synchronize on.
 * <p>
 * In our exercise, we will try to define a global order on the Krasi objects,
 * to allow us to always lock in the same order.
 *
 * @author Heinz Kabutz
 */
public class Krasi implements Comparable<Krasi> {

    private static final AtomicLong id_generator = new AtomicLong(0);

    private final long kid;

    public Krasi() {
        kid = id_generator.getAndIncrement();
    }

    @Override
    public int compareTo(Krasi o) {
        return Long.compare(kid, o.kid);
    }
}

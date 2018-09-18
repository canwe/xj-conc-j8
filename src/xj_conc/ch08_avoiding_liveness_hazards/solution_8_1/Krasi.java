package xj_conc.ch08_avoiding_liveness_hazards.solution_8_1;

import java.util.concurrent.atomic.*;

/**
 * "Krasi" means wine in Greek, which is where the philosophers used to live
 * many thousands of years ago.  We use the Krasi objects as locks to
 * synchronize on.
 * <p>
 * In our solution, we define a cupNumber inside the Krasi class, which we use
 * to sort on.  Note that the Integer.compare() method is a new Java 7 method.
 *
 * @author Heinz Kabutz
 */
public class Krasi implements Comparable<Krasi> {
    private final static AtomicLong nextCupNumber = new AtomicLong();
    private final long cupNumber = nextCupNumber.incrementAndGet();

    @Override
    public int compareTo(Krasi o) {
        return Long.compare(cupNumber, o.cupNumber);
    }

    @Override
    public String toString() {
        return "Krasi{cupNumber=" + cupNumber + '}';
    }
}

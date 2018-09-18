package xj_conc.ch11_explicit_locks.solution_11_2b;

import java.util.concurrent.locks.*;

/**
 * "Krasi" means wine in Greek, which is where the philosophers used to live
 * many thousands of years ago.  We can call lock() directly on Krasi, as we
 * have subclassed ReentrantLock.
 * <p>
 * In our exercise, we will try to solve the deadlock by calling tryLock()
 * instead of lock() and retrying if we are not successful.
 * <p>
 * DO NOT CHANGE THIS CODE!
 *
 * @author Heinz Kabutz
 */
@SuppressWarnings("serial")
public class Krasi extends ReentrantLock {
}

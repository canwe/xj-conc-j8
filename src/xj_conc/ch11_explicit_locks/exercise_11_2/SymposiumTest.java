package xj_conc.ch11_explicit_locks.exercise_11_2;


import org.junit.*;
import xj_conc.ch08_avoiding_liveness_hazards.exercise_8_1.*;
import xj_conc.util.*;

import static org.junit.Assert.*;

/**
 * Tests whether the Symposium ends in a deadlock.  You might need to run it a
 * few times on your machine before the deadlock surfaces.
 * <p>
 * DO NOT CHANGE THIS CODE!
 *
 * @author Heinz Kabutz
 */
public class SymposiumTest {
    private volatile ThinkerStatus status;

    @Test
    public void runSymposium() throws InterruptedException {
        DeadlockTester tester = new DeadlockTester();
        try {
            tester.checkThatCodeDoesNotDeadlock(() -> {
                    try {
                        Symposium symposium = new Symposium(5);
                        status = symposium.run();
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            );
            assertEquals(ThinkerStatus.HAPPY_THINKER, status);
        } catch (DeadlockError er) {
            fail("One of the threads you started has deadlocked - " +
                er.getThread());
        }
    }
}

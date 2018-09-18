package xj_conc.ch01_introduction.exercise_1_1;

import net.jcip.annotations.*;
import org.junit.*;

import static xj_conc.util.TestHelpers.*;

/**
 * DO NOT CHANGE.
 */
public class PersonTest {
    @Test
    public void testPerson1() {
        assertTypeIsAnnotated(Immutable.class, Person1.class);
    }

    @Test
    public void testPerson2() {
        assertTypeIsAnnotated(NotThreadSafe.class, Person2.class);
    }

    @Test
    public void testPerson3() throws NoSuchFieldException {
        assertTypeIsAnnotated(ThreadSafe.class, Person3.class);
        assertFieldIsAnnotated(GuardedBy.class, Person3.class, "age");
    }
}

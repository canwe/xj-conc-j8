package xj_conc.ch01_introduction.solution_1_1;

import net.jcip.annotations.*;

/**
 * This class is mutable, but the field data is guarded by synchronization on
 * "this".  Thus it is thread-safe.
 */
@ThreadSafe
public class Person3 {
    private final String firstName;
    private final String surname;
    @GuardedBy("this")
    private int age;

    public Person3(String firstName,
                   String surname, int age) {
        this.firstName = firstName;
        this.surname = surname;
        this.age = age;
    }

    public synchronized void birthday() {
        age = age + 1;
    }
}
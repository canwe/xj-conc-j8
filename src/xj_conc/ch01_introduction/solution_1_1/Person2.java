package xj_conc.ch01_introduction.solution_1_1;

import net.jcip.annotations.*;

/**
 * The age field is mutable and is not protected by synchronization.  This class
 * is thus not thread-safe, because two threads calling birthday() at the same
 * time could cause a race condition.  On the other hand, a birthday should only
 * happen once a year ;-)
 */
@NotThreadSafe
public class Person2 {
    private final String firstName;
    private final String surname;
    private int age;

    public Person2(String firstName,
                   String surname, int age) {
        this.firstName = firstName;
        this.surname = surname;
        this.age = age;
    }

    public void birthday() {
        age = age + 1;
    }
}
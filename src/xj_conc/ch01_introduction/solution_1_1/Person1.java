package xj_conc.ch01_introduction.solution_1_1;

import net.jcip.annotations.*;

/**
 * All the fields are final and the objects they point to are also immutable.
 * Thus this class is immutable.
 */
@Immutable
public class Person1 {
    private final String firstName;
    private final String surname;
    private final int age;

    public Person1(String firstName,
                   String surname, int age) {
        this.firstName = firstName;
        this.surname = surname;
        this.age = age;
    }
}
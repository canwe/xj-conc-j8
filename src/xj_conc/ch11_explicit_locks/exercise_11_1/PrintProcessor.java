package xj_conc.ch11_explicit_locks.exercise_11_1;

/**
 * DO NOT CHANGE.
 */
public class PrintProcessor implements Processor<Object> {
    public boolean process(Object o) {
        System.out.println(">>> " + o);
        return true;
    }
}

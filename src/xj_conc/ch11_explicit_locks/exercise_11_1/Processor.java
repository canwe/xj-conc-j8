package xj_conc.ch11_explicit_locks.exercise_11_1;

/**
 * DO NOT CHANGE.
 */
@FunctionalInterface
public interface Processor<E> {
    boolean process(E e);
}

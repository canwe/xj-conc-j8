package xj_conc.ch11_explicit_locks.exercise_11_1;

import java.util.*;

/**
 * DO NOT CHANGE.
 */
public class CompositeProcessor<E>
    implements Processor<E> {
    private final List<Processor<? super E>> processors =
        new ArrayList<>();

    public void add(Processor<? super E> processor) {
        processors.add(processor);
    }

    public boolean process(E e) {
        for (Processor<? super E> processor : processors) {
            if (!processor.process(e)) return false;
        }
        return true;
    }
}

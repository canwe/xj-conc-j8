package xj_conc.ch03_building_blocks.exercise_3_2;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This task should be comparable according to priority and insertion order.
 * Try and understand what
 */
class PrioritizedFutureTask<T> extends FutureTask<T> implements Comparable<T> {

    private static final AtomicInteger i = new AtomicInteger(0);
    private final Priority priority;
    private final int inserted = i.incrementAndGet();

    public PrioritizedFutureTask(Runnable runnable, T result, Priority priority) {
        super(runnable, result);
        this.priority = priority;
    }

    public PrioritizedFutureTask(Callable<T> callable, Priority priority) {
        super(callable);
        this.priority = priority;
    }

    @Override
    public int compareTo(T o) {

        if (! (o instanceof PrioritizedFutureTask)) {
            return -1;
        }

        PrioritizedFutureTask<T> other = (PrioritizedFutureTask<T>) o;

        if (this.priority.equals(other.priority)) {
            return this.inserted - other.inserted;
        }
        return this.priority.compareTo(other.priority);
    }
}

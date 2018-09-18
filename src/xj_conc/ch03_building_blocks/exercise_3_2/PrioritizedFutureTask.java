package xj_conc.ch03_building_blocks.exercise_3_2;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This task should be comparable according to priority and insertion order.
 * Try and understand what
 */
class PrioritizedFutureTask<T> extends FutureTask<T> implements Comparable<PrioritizedFutureTask> {

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
    public int compareTo(PrioritizedFutureTask o) {
        if (this.priority.equals(o.priority)) {
            return this.inserted - o.inserted;
        }
        return this.priority.compareTo(o.priority);
    }
}

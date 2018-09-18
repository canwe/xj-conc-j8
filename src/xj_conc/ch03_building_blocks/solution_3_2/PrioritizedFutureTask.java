package xj_conc.ch03_building_blocks.solution_3_2;

import xj_conc.ch03_building_blocks.exercise_3_2.*;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * We need to sort the tasks according to the priority AND the insertion order.
 */
class PrioritizedFutureTask<T> extends FutureTask<T>
    implements Comparable<PrioritizedFutureTask<T>> {
    private final Priority priority;
    private final static AtomicInteger nextSequence = new AtomicInteger(0);
    private final long sequence = nextSequence.incrementAndGet();

    public PrioritizedFutureTask(Runnable runnable, T result, Priority priority) {
        super(runnable, result);
        this.priority = priority;
    }

    public PrioritizedFutureTask(Callable<T> callable, Priority priority) {
        super(callable);
        this.priority = priority;
    }

    public int compareTo(PrioritizedFutureTask o) {
        int result = priority.compareTo(o.priority);
        if (result != 0) return result;
        return Long.compare(sequence, o.sequence);
    }
}

package xj_conc.ch12_building_custom_synchronizers.exercise_12_1;

import net.jcip.annotations.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

@ThreadSafe
public class FutureResultIterable<V> implements Iterable<Future<V>> {
    private final ExecutorService delegate;
    private final CompletionService<V> service;
    private final ExecutorService resultFetcher = Executors.newSingleThreadExecutor();

    private final AtomicInteger numberOfSubmittedTasks = new AtomicInteger(0);
    @GuardedBy("completedJobs")
    private final List<Future<V>> completedJobs = new ArrayList<>();

    /**
     * DO NOT CHANGE.
     */
    public FutureResultIterable() {
        delegate = Executors.newCachedThreadPool();
        service = new ExecutorCompletionService<>(delegate);
    }

    /**
     * DO NOT CHANGE.
     */
    public void submit(Callable<V> task) {
        service.submit(task);
        if (numberOfSubmittedTasks.getAndIncrement() == 0) {
            startResultFetcherThread(); // to avoid starting it in the constructor
        }
    }

    private void startResultFetcherThread() {
        resultFetcher.submit(() -> {
            /* TODO: implement the following pseudocode in Java
            while(there_are_more_jobs_to_fetch || we_have_not_been_interrupted) {
              take_the_next_job_from_the_completion_service
              add_it_to_the_completed_jobs_list_with_correct_synchronization
              signal_or_notify_that_we_have_added_it
            }
            */
        });
    }

    public Iterator<Future<V>> iterator() {
        return new Iterator<Future<V>>() {
            private int pos;

            public boolean hasNext() {
                // TODO: compare our current position to number of submitted tasks
                throw new UnsupportedOperationException("todo");
            }

            public Future<V> next() {
                /* TODO: implement the following pseudocode in Java
                    acquire lock;
                    // BLOCK-UNTIL: pos < completedJobs.size()
                    while(our_pre_condition_is_false) {
                        wait_until_condition_might_change
                        if_interrupted_save_for_later_and_self_interrupt_before_return
                    }
                    return_the_next_future_from_the_completed_jobs_and_increment_pos
                    release lock;
                */
                throw new UnsupportedOperationException("todo");
            }
        };
    }

    /**
     * DO NOT CHANGE.
     */
    public void shutdown() {
        delegate.shutdown();
        resultFetcher.shutdownNow();
    }

    /**
     * DO NOT CHANGE.
     */
    public boolean isTerminated() {
        return resultFetcher.isTerminated() && delegate.isTerminated();
    }
}

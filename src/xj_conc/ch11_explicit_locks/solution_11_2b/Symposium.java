package xj_conc.ch11_explicit_locks.solution_11_2b;

import xj_conc.ch08_avoiding_liveness_hazards.exercise_8_1.*;

import java.util.concurrent.*;

/**
 * At the symposium, we create a bunch of thinkers and place cups of wine
 * between them.  We then run them in a cached thread pool.  Unfortunately when
 * all the Thinker instances try to drink at the same time, they cause a
 * deadlock.
 * <p>
 * DO NOT CHANGE THIS CODE!
 *
 * @author Heinz Kabutz
 */
public class Symposium {
    private final Krasi[] cups;
    private final Thinker[] thinkers;

    public Symposium(int delegates) {
        cups = new Krasi[delegates];
        thinkers = new Thinker[delegates];
        for (int i = 0; i < cups.length; i++) {
            cups[i] = new Krasi();
        }
        for (int i = 0; i < delegates; i++) {
            Krasi right = cups[i];
            Krasi left = cups[(i + 1) % delegates];
            thinkers[i] = new Thinker(i, left, right);
        }
    }

    public ThinkerStatus run() throws InterruptedException {
        // do this after we created the symposium, so that we do not
        // let the reference to the Symposium escape.
        ExecutorService exec = Executors.newCachedThreadPool();
        CompletionService<ThinkerStatus> results =
            new ExecutorCompletionService<>(exec);
        for (Thinker thinker : thinkers) {
            results.submit(thinker);
        }
        ThinkerStatus result = ThinkerStatus.HAPPY_THINKER;
        System.out.println("Waiting for results");
        for (@SuppressWarnings("unused") Thinker thinker : thinkers) {
            try {
                ThinkerStatus status = results.take().get();
                System.out.println(status);
                if (status == ThinkerStatus.UNHAPPY_THINKER) {
                    result = ThinkerStatus.UNHAPPY_THINKER;
                }
            } catch (ExecutionException e) {
                e.getCause().printStackTrace();
                result = ThinkerStatus.UNHAPPY_THINKER;
            }
        }
        exec.shutdown();

        for (int i = 0; i < thinkers.length; i++) {
            System.out.printf("thinker[%d] = %,3d%n", i,
                thinkers[i].getRetry());
        }
        return result;
    }
}

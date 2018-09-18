package xj_conc.ch14_memory.exercise_14_1_1;

import xj_conc.util.*;

import java.util.concurrent.*;

/**
 * Run the ObjectCreation class with -XX:+UseParallelOldGC -Xmx1g
 * to get a baseline speed.  Check the logs for full GCs
 * (-Xloggc:parold.vgc).  See if you can reduce the full GCs by
 * setting the "New Ratio" (-XX:NewRatio=n) or the "Survivor
 * Ratio" (-XX:SurvivorRatio=n).  See also what happens when you
 * set the maximum GC pause time lower or higher
 * (-XX:MaxGCPauseMillis=num).
 * <p>
 * Next run it with -XX:+UseConcMarkSweepGC -Xmx1g
 * Again, try improve by setting the new and survivor ratios
 * <p>
 * We then run it with -XX:+UseG1GC -Xmx1g
 * <p>
 * Lastly, we run the tests again with -Xmx4g
 */
public class ObjectCreation {
    private static final int REPEATS = 1_000_000;
    private volatile static int MEMORY = 1 << 16;
    private static final int MAX_ARRAY_SIZE = 5_000;
    private static byte[][] memory = new byte[MEMORY][];
    public static final int MIN_ARRAY_SIZE = 1000;
    private static int[] randomSizes =
        ThreadLocalRandom.current().
            ints(MEMORY, MIN_ARRAY_SIZE, MAX_ARRAY_SIZE).
            parallel().toArray();

    public static void main(String... args) {
        SuperSimpleGCParser.showGCLogSummaryAtExit();
        long bestTime = Long.MAX_VALUE;
        for (int i = 0; i < 10; i++) {
            bestTime = Math.min(test(), bestTime);
        }
        System.out.println("bestTime = " + bestTime);
    }

    private static long test() {
        long time = System.currentTimeMillis();
        for (int i = 0; i < REPEATS; i++) {
            int mask = MEMORY - 1;
            memory[i & mask] = new byte[randomSizes[i & mask]];
        }
        time = System.currentTimeMillis() - time;
        System.out.println("time = " + time);
        return time;
    }
}

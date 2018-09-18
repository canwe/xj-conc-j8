package xj_conc.ch06_applying_thread_pools.solution_6_3;

import java.util.concurrent.*;

public class ThreadPoolFactory {
    private static final int DEFAULT_MAXIMUM_THREADS = 1000;

    /**
     * This should return a fixed thread pool with the correct number of threads,
     * according to the formula given in section 8.2.
     *
     * @param cpus           number of physical cores available.  Cannot be less
     *                       than 1.
     * @param utilization    larger than 0.0 and less or equal to 1.0.  Represents
     *                       how busy we want to keep the CPUs.
     * @param waitTime       greater or equal to 0.0 and less than 1.0.  Amount of
     *                       time that each thread would need to wait.
     * @param maximumThreads number of core threads cannot exceed this number.
     */
    public ExecutorService newFixedThreadPool(
        int cpus, double utilization, double waitTime, int maximumThreads) {
        checkParameters(cpus, utilization, waitTime, maximumThreads);

        double waitOverCompute = waitTime / (1 - waitTime);
        double threads = cpus * utilization * (1 + waitOverCompute);
        System.out.println("threads = " + threads);
        int numberOThreads = (int) Math.ceil(threads);
        if (numberOThreads > maximumThreads)
            numberOThreads = maximumThreads;
        System.out.println("numberOThreads = " + numberOThreads);
        return Executors.newFixedThreadPool(numberOThreads);
    }

    /**
     * DO NOT CHANGE.
     */
    private void checkParameters(
        int cpus, double utilization, double waitTime, int maximumThreads) {
        if (cpus < 1) throw new IllegalArgumentException();
        if (utilization <= 0.0) throw new IllegalArgumentException();
        if (utilization > 1.0) throw new IllegalArgumentException();
        if (waitTime < 0.0) throw new IllegalArgumentException();
        if (waitTime >= 1.0) throw new IllegalArgumentException();
        if (maximumThreads < 1) throw new IllegalArgumentException();
    }

    /**
     * DO NOT CHANGE.
     */
    public ExecutorService newFixedThreadPool(
        int cpus, double utilization, double waitTime) {
        return newFixedThreadPool(cpus, utilization, waitTime,
            DEFAULT_MAXIMUM_THREADS);
    }
}

package xj_conc.ch06_applying_thread_pools.exercise_6_2;

import javax.management.*;
import java.lang.management.*;
import java.util.Map;
import java.util.concurrent.*;

public class ThreadPoolStatsManager {
    private static final MBeanServer BEAN_SERVER = ManagementFactory.getPlatformMBeanServer();

    //map that manages the ThreadPoolStats instances.
    private final static Map<ThreadPoolStats, ObjectName> threadPools = new ConcurrentHashMap<>();

    public static void register(ThreadPoolExecutor pool, String name) {
        try {
            ObjectName objectName = new ObjectName(
                "java.util.concurrent.ThreadPoolExecutor:type=ThreadPoolStats-" + name);
            ThreadPoolStats stats = new ThreadPoolStats(pool);
            threadPools.put(stats, objectName);
            BEAN_SERVER.registerMBean(stats, objectName);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static void unregister(ThreadPoolStats stats) {
        try {
            ObjectName objectName = threadPools.remove(stats);
            if (objectName != null) {
                BEAN_SERVER.unregisterMBean(objectName);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}

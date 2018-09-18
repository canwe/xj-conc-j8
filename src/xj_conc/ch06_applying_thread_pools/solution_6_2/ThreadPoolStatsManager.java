package xj_conc.ch06_applying_thread_pools.solution_6_2;

import javax.management.*;
import java.lang.management.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * We used a synchronized weak hash map to hold keys of ThreadPoolStats objects
 * and as values the ObjectName.
 */
public class ThreadPoolStatsManager {
    private static final MBeanServer BEAN_SERVER = ManagementFactory.getPlatformMBeanServer();
    private static final Map<ThreadPoolStats, ObjectName> registeredPools =
        Collections.synchronizedMap(new WeakHashMap<>());

    public static void register(ThreadPoolExecutor pool, String name) {
        try {
            ObjectName objectName = new ObjectName(
                "java.util.concurrent.ThreadPoolExecutor:type=ThreadPoolStats-" + name);
            ThreadPoolStats stats = new ThreadPoolStats(pool);
            registeredPools.put(stats, objectName);
            BEAN_SERVER.registerMBean(stats, objectName);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static void unregister(ThreadPoolStats pool) {
        try {
            ObjectName objectName = registeredPools.remove(pool);
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

package xj_conc.ch06_applying_thread_pools.exercise_6_2;

import javax.management.*;
import java.lang.management.*;
import java.util.concurrent.*;

/**
 * TODO: Add a map that manages the ThreadPoolStats instances.
 */
public class ThreadPoolStatsManager {
    private static final MBeanServer BEAN_SERVER = ManagementFactory.getPlatformMBeanServer();

    public static void register(ThreadPoolExecutor pool, String name) {
        try {
            ObjectName objectName = new ObjectName(
                "java.util.concurrent.ThreadPoolExecutor:type=ThreadPoolStats-" + name);
            ThreadPoolStats stats = new ThreadPoolStats(pool);
            // TODO: Put the ThreadPoolStats into a map, with the ObjectName as value
            // TODO: This will allow you to unregister the bean easily later
            BEAN_SERVER.registerMBean(stats, objectName);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static void unregister(ThreadPoolStats stats) {
        // TODO: Find the ObjectName for this pool
        try {
            ObjectName objectName = null; // TODO: Retrieve the ObjectName from the map
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

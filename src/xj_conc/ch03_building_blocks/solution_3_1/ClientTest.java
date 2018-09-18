package xj_conc.ch03_building_blocks.solution_3_1;

import org.junit.*;
import xj_conc.ch03_building_blocks.exercise_3_1.*;
import xj_conc.util.*;

import java.util.concurrent.*;
import java.util.stream.*;

import static org.junit.Assert.*;

/**
 * DO NOT CHANGE.
 */
public class ClientTest {
    @Test
    public void checkForConcurrentModificationException() throws ExecutionException, InterruptedException {
        SuperSimpleGCParser.showGCLogSummaryAtExit();
        ExecutorService pool = Executors.newCachedThreadPool();
        Future<?> future = pool.submit(() -> {
            long time = System.currentTimeMillis();
            Client client = new Client();
            IntStream.range(0, 100_000_000).forEach(
                i -> client.checkAlerts()
            );
            time = System.currentTimeMillis() - time;
            System.out.println("checkForConcurrentModificationException() iteration done in " + time + "ms");
        });
        pool.submit(() -> {
            long time = System.currentTimeMillis();
            AlertProvider prov = AlertProvider.getInstance();
            Alert alert = new Alert("fly loose in the server room", AlertLevel.GREEN);
            IntStream.range(0, 100_000_000).forEach(
                i -> {
                    prov.addAlert(alert);
                    prov.removeAlert(alert);
                }
            );
            time = System.currentTimeMillis() - time;
            System.out.println("checkForConcurrentModificationException() modification done in " + time + "ms");
        });
        pool.shutdown();
        try {
            future.get();
        } catch (ExecutionException e) {
            e.getCause().printStackTrace();
            fail("Exception occurred: " + e.getCause());
        }
        while (!pool.awaitTermination(1, TimeUnit.MINUTES)) ;
    }

    @Test
    public void severalAlarms() {
        Alert[] alerts = {
            new Alert("Submarine approaching", AlertLevel.RED),
            new Alert("Grexit", AlertLevel.GREEN),
            new Alert("Italian Cruise Captain", AlertLevel.ORANGE)
        };
        Stream.of(alerts).forEach(
            alert -> AlertProvider.getInstance().addAlert(alert));
        Stream.of(alerts).forEach(
            alert -> AlertProvider.getInstance().removeAlert(alert));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testAlertsAreUnmodifiable() {
        AlertProvider.getInstance().getAlerts().clear();
    }
}

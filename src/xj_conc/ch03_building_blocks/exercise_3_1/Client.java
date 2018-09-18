package xj_conc.ch03_building_blocks.exercise_3_1;

import java.util.*;

/**
 * TODO: We need to avoid the ConcurrentModification exception.
 */
public class Client {
    private final AlertProvider alertProvider = AlertProvider.getInstance();

    public void checkAlerts() {
        Collection<Alert> alerts = alertProvider.getAlerts(); // <---I get a ConcurrentModificationException here
        synchronized (alerts) {
            alerts.stream().
                filter(alert -> alert.getLevel() != AlertLevel.GREEN).
                forEach(alert -> System.out.println("Alert level " + alert.getLevel()));
        }
    }
}

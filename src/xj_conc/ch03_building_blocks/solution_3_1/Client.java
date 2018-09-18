package xj_conc.ch03_building_blocks.solution_3_1;

import xj_conc.ch03_building_blocks.exercise_3_1.*;

import java.util.*;

/**
 * We did not need to change the client, expect maybe remove the pointless
 * synchronized.  Client-side locking would not work, since we do not have a
 * handle to the synchronized collection.  The safest way of solving the
 * ConcurrentModificationException is to either use a thread-safe collection or
 * to return copies of the synchronized collection. In other words, solve it
 * inside the AlertProvider class, rather than here. Also, the
 * ConcurrentModificationException occurs during the for() loop, not where the
 * comment indicated.  However, when my client sent me the code, he thought it
 * was occurring in the getAlerts() method.
 */
public class Client {
    private final AlertProvider alertProvider = AlertProvider.getInstance();

    public void checkAlerts() {
        Collection<Alert> alerts = alertProvider.getAlerts();
        alerts.stream().
            filter(alert -> alert.getLevel() != AlertLevel.GREEN).
            forEach(alert -> System.out.println("Alert level " + alert.getLevel()));
    }
}

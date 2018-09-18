package xj_conc.ch03_building_blocks.exercise_3_1;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * TODO: We need to avoid the ConcurrentModification exception in Client class.
 *
 * @see Client
 */
public class AlertProvider {
    private final Collection<Alert> alerts = new CopyOnWriteArrayList<>();
    // also can be ConcurrentHashMap.newKeySet() or any other thread safe collection

    public Collection<Alert> getAlerts() {
        return Collections.unmodifiableCollection(alerts);
    }

    private AlertProvider() {
    }

    private final static AlertProvider instance = new AlertProvider();

    public static AlertProvider getInstance() {
        return instance;
    }

    public boolean addAlert(Alert alert) {
        return alerts.add(alert);
    }

    public boolean removeAlert(Alert alert) {
        return alerts.remove(alert);
    }
}

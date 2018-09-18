package xj_conc.ch03_building_blocks.solution_3_1;

import xj_conc.ch03_building_blocks.exercise_3_1.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * The reason we had a problem in the old AlertProvider was that the "alerts"
 * List changed whilst we were iterating, causing an error in the getAlerts()
 * method.  We can solve that by either making a copy of the collection whenever
 * we return the collection of alerts, or by using something like a
 * CopyOnWriteArrayList.  We could also use a ConcurrentLinkedQueue.
 */
public class AlertProvider {
    // there are lots of other options, such as
    // ConcurrentLinkedQueue, ConcurrentSkipListSet, etc.
    private final List<Alert> alerts = new CopyOnWriteArrayList<>(); // 9676ms / 10714ms


//    private final Collection<Alert> alerts = new ConcurrentSkipListSet<>( // 13144ms / 16276ms
//             Comparator.comparing(System::identityHashCode)
//     );
//     private final Collection<Alert> alerts = Collections.newSetFromMap(new ConcurrentHashMap<>()); // 13923ms / 10060ms
//     private final Collection<Alert> alerts = new ConcurrentLinkedQueue<>(); // 10652ms / 10652ms

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
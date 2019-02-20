package xj_conc.ch03_building_blocks.exercise_3_1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * TODO: We need to avoid the ConcurrentModification exception in Client class.
 *
 * @see Client
 */
public class AlertProvider {

    //copyonwritearraylist
    //GCStatistics for filename.vgc
    //	durationOfLog=PT12.12S
    //	numberOfGCs=185
    //	numberOfYoungGCs=185
    //	numberOfOldGCs=0
    //	memoryReclaimedDuringYoung=22.409GB
    //	memoryReclaimedDuringOld=0.000B
    //	maxHeapAfterGC=1.320MB
    //	totalMemoryAllocated=22.410GB
    //	averageCreationRate=1.85GB/s
    //	timeInGCs=PT0.0828537S
    //	timeInYoungGCs=PT0.0828537S
    //	averageTimeInYoungGCs=DoubleSummaryStatistics{count=185, sum=0.082854, min=0.000335, average=0.000448, max=0.001754}
    //	timeInOldGCs=PT0S
    //	percentageOfTimeInGC=0.68%

    //ConcurrentHashMap.newKeySet();
    //GCStatistics for filename.vgc
    //	durationOfLog=PT17.11S
    //	numberOfGCs=291
    //	numberOfYoungGCs=291
    //	numberOfOldGCs=0
    //	memoryReclaimedDuringYoung=23.803GB
    //	memoryReclaimedDuringOld=0.000B
    //	maxHeapAfterGC=1.305MB
    //	totalMemoryAllocated=23.804GB
    //	averageCreationRate=1.39GB/s
    //	timeInGCs=PT0.1287135S
    //	timeInYoungGCs=PT0.1287135S
    //	averageTimeInYoungGCs=DoubleSummaryStatistics{count=291, sum=0.128714, min=0.000344, average=0.000442, max=0.001935}
    //	timeInOldGCs=PT0S
    //	percentageOfTimeInGC=0.75%

    //Collections.synchronizedList(new ArrayList<>(100));
    //GCStatistics for filename.vgc
    //	durationOfLog=PT18.826S
    //	numberOfGCs=214
    //	numberOfYoungGCs=214
    //	numberOfOldGCs=0
    //	memoryReclaimedDuringYoung=22.290GB
    //	memoryReclaimedDuringOld=0.000B
    //	maxHeapAfterGC=1.258MB
    //	totalMemoryAllocated=22.291GB
    //	averageCreationRate=1.18GB/s
    //	timeInGCs=PT0.0986452S
    //	timeInYoungGCs=PT0.0986452S
    //	averageTimeInYoungGCs=DoubleSummaryStatistics{count=214, sum=0.098645, min=0.000355, average=0.000461, max=0.001803}
    //	timeInOldGCs=PT0S
    //	percentageOfTimeInGC=0.52%

    //new LinkedBlockingQueue<>();
    //GCStatistics for filename.vgc
    //	durationOfLog=PT44.726S
    //	numberOfGCs=539
    //	numberOfYoungGCs=539
    //	numberOfOldGCs=0
    //	memoryReclaimedDuringYoung=26.238GB
    //	memoryReclaimedDuringOld=0.000B
    //	maxHeapAfterGC=1.336MB
    //	totalMemoryAllocated=26.239GB
    //	averageCreationRate=600.74MB/s
    //	timeInGCs=PT0.2294977S
    //	timeInYoungGCs=PT0.2294977S
    //	averageTimeInYoungGCs=DoubleSummaryStatistics{count=539, sum=0.229498, min=0.000337, average=0.000426, max=0.004925}
    //	timeInOldGCs=PT0S
    //	percentageOfTimeInGC=0.51%

    //new ConcurrentLinkedQueue<>();
    //GCStatistics for filename.vgc
    //	durationOfLog=PT13.714S
    //	numberOfGCs=188
    //	numberOfYoungGCs=188
    //	numberOfOldGCs=0
    //	memoryReclaimedDuringYoung=25.111GB
    //	memoryReclaimedDuringOld=0.000B
    //	maxHeapAfterGC=1.320MB
    //	totalMemoryAllocated=25.112GB
    //	averageCreationRate=1.83GB/s
    //	timeInGCs=PT0.0844763S
    //	timeInYoungGCs=PT0.0844763S
    //	averageTimeInYoungGCs=DoubleSummaryStatistics{count=188, sum=0.084476, min=0.000324, average=0.000449, max=0.001847}
    //	timeInOldGCs=PT0S
    //	percentageOfTimeInGC=0.62%

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

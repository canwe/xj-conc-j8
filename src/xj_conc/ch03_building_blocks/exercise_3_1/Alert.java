package xj_conc.ch03_building_blocks.exercise_3_1;

/**
 * DO NOT CHANGE.
 */
public class Alert {
    private final String situation;
    private final AlertLevel level;

    public Alert(String situation, AlertLevel level) {
        this.situation = situation;
        this.level = level;
    }

    public AlertLevel getLevel() {
        return level;
    }

    public String toString() {
        return "Alert: " + situation + " code " + level;
    }
}

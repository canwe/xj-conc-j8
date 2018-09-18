package xj_conc.ch07_fork_join.exercise_7_1;

/**
 * DO NOT CHANGE.
 */
public enum Instrument {
    PIANO_BOTTOM, PIANO_TOP, VIOLIN_BODY, VIOLIN_CHORD, CLARINET_MOUTH,
    CLARINET_END, CLARINET_BAD, HORN_PIPES, HORN_MOUTH;

    public boolean matches(Instrument other) {
        switch (this) {
            case PIANO_BOTTOM:
                return other == PIANO_TOP;
            case PIANO_TOP:
                return other == PIANO_BOTTOM;
            case VIOLIN_BODY:
                return other == VIOLIN_CHORD;
            case VIOLIN_CHORD:
                return other == VIOLIN_BODY;
            case CLARINET_MOUTH:
                return other == CLARINET_END;
            case CLARINET_END:
                return other == CLARINET_MOUTH;
            case CLARINET_BAD:
                return false;
            case HORN_MOUTH:
                return other == HORN_PIPES;
            case HORN_PIPES:
                return other == HORN_MOUTH;
            default:
                throw new IllegalStateException();
        }
    }
}

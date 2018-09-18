package xj_conc.ch07_fork_join.exercise_7_1;

import java.util.*;

/**
 * DO NOT CHANGE.
 */
public class Tile {
    public static boolean DEBUG = false;
    private final String filename;
    private final EnumMap<Direction, Instrument> instruments;
    private final Direction direction;

    public Tile(String filename, Direction direction, Instrument... instruments) {
        this.instruments = new EnumMap<>(Direction.class);
        this.filename = filename;
        this.direction = direction;
        this.instruments.put(Direction.NORTH, instruments[0]);
        this.instruments.put(Direction.EAST, instruments[1]);
        this.instruments.put(Direction.SOUTH, instruments[2]);
        this.instruments.put(Direction.WEST, instruments[3]);
    }

    private Tile(String filename, Direction direction, EnumMap<Direction, Instrument> instruments) {
        this.filename = filename;
        this.direction = direction;
        this.instruments = instruments;
    }

    public Instrument getInstrumentAt(Direction place) {
        if (this.direction == Direction.NORTH) {
            return instruments.get(place);
        }
        if (this.direction == Direction.SOUTH) {
            return instruments.get(place.opposite());
        }
        if (this.direction == Direction.WEST) {
            switch (place) {
                case NORTH:
                    return instruments.get(Direction.WEST);
                case EAST:
                    return instruments.get(Direction.NORTH);
                case SOUTH:
                    return instruments.get(Direction.EAST);
                case WEST:
                    return instruments.get(Direction.SOUTH);
            }
        }
        if (this.direction == Direction.EAST) {
            switch (place) {
                case NORTH:
                    return instruments.get(Direction.EAST);
                case EAST:
                    return instruments.get(Direction.SOUTH);
                case SOUTH:
                    return instruments.get(Direction.WEST);
                case WEST:
                    return instruments.get(Direction.NORTH);
            }
        }
        throw new IllegalStateException();
    }

    public boolean matches(Direction place, Tile other) {
        // 2 + 1
        Instrument instrument1 = getInstrumentAt(place);
        Instrument instrument2 = other.getInstrumentAt(place.opposite());
        if (DEBUG) {
            System.out.println("instrument1 = " + instrument1);
            System.out.println("instrument2 = " + instrument2);
        }
        return instrument1.matches(instrument2);
    }

    public String getFilename() {
        return filename;
    }

    public Direction getDirection() {
        return direction;
    }

    public Tile rotateTo(Direction direction) {
        if (direction == this.direction) {
            return this;
        }
        return new Tile(filename, direction, instruments);
    }

    public String toString() {
        return filename.replaceFirst(".*/", "").replaceFirst(".jpg", "") +
            "(" + direction.toString().charAt(0) + ")";
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tile tile = (Tile) o;

        if (filename != null ? !filename.equals(tile.filename) : tile.filename != null)
            return false;

        return true;
    }

    public int hashCode() {
        return filename != null ? filename.hashCode() : 0;
    }
}

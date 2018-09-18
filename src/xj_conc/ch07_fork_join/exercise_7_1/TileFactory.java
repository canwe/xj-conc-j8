package xj_conc.ch07_fork_join.exercise_7_1;

import static xj_conc.ch07_fork_join.exercise_7_1.Instrument.*;

/**
 * DO NOT CHANGE.
 */
public class TileFactory {
    private static final String PATH = "/xj_conc/ch07_fork_join/exercise_7_1/images/";

    public static Tile[] makeTiles() {
        return new Tile[]{
            new Tile(PATH + "tile1.jpg", Direction.NORTH, CLARINET_BAD, CLARINET_END, VIOLIN_BODY, HORN_MOUTH),
            new Tile(PATH + "tile2.jpg", Direction.NORTH, HORN_MOUTH, CLARINET_BAD, PIANO_TOP, CLARINET_END),
            new Tile(PATH + "tile3.jpg", Direction.NORTH, HORN_PIPES, VIOLIN_BODY, CLARINET_MOUTH, PIANO_BOTTOM),
            new Tile(PATH + "tile4.jpg", Direction.NORTH, PIANO_TOP, CLARINET_END, VIOLIN_CHORD, HORN_MOUTH),
            new Tile(PATH + "tile5.jpg", Direction.NORTH, HORN_PIPES, VIOLIN_CHORD, PIANO_TOP, HORN_PIPES),
            new Tile(PATH + "tile6.jpg", Direction.NORTH, HORN_MOUTH, VIOLIN_CHORD, VIOLIN_BODY, PIANO_BOTTOM),
            new Tile(PATH + "tile7.jpg", Direction.NORTH, PIANO_BOTTOM, CLARINET_MOUTH, PIANO_TOP, VIOLIN_BODY),
            new Tile(PATH + "tile8.jpg", Direction.NORTH, VIOLIN_BODY, CLARINET_MOUTH, PIANO_BOTTOM, HORN_MOUTH),
            new Tile(PATH + "tile9.jpg", Direction.NORTH, PIANO_TOP, CLARINET_BAD, VIOLIN_BODY, HORN_PIPES),
        };
    }
}

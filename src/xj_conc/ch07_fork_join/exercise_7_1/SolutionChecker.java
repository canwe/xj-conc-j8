package xj_conc.ch07_fork_join.exercise_7_1;

/**
 * DO NOT CHANGE.
 */
public class SolutionChecker {
    public static boolean check(Tile... tiles) {
        return check(3, 3, tiles);
    }

    public static boolean check(int rows, int cols, Tile[] tiles) {
        boolean matches = true;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols - 1; col++) {
                matches = matches && match(tiles, (row * cols) + col, (row * cols) + col + 1, Direction.EAST);
            }
        }
        for (int row = 0; row < rows - 1; row++) {
            for (int col = 0; col < cols; col++) {
                matches = matches && match(tiles, (row * cols) + col, (row * cols) + col + cols, Direction.SOUTH);
            }
        }
        return matches;
    }

    private static boolean match(Tile[] tiles, int i0, int i1, Direction place) {
        boolean reachedEnd = i0 >= tiles.length || i1 >= tiles.length
            || tiles[i0] == null || tiles[i1] == null;
        return reachedEnd
            || tiles[i0].matches(place, tiles[i1]);
    }
}

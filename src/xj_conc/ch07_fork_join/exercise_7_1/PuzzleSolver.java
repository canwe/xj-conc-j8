package xj_conc.ch07_fork_join.exercise_7_1;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

/**
 * The algorithm tries putting tiles into each of the spaces and rotating them.
 * As soon as it finds two tiles that match, it tries to find three that match.
 * It recursively goes through the entire board until it either finds a solution
 * or discovers that it needs to back out again.
 */
public class PuzzleSolver {
    private final static Tile[] tiles = TileFactory.makeTiles();

    public List<Tile[]> findSolutions() {
        // TODO: instead, we will create a PuzzleSolverTask and call invoke()
        return findSolutionsRecursive(new Tile[tiles.length]);
    }

    private List<Tile[]> findSolutionsRecursive(Tile[] previousTiles) {
        int pos = 0;
        for (Tile tile : previousTiles) {
            if (tile == null) break;
            pos++;
        }
        if (pos == 9) {
            return Collections.singletonList(previousTiles);
        }

        List<Tile[]> results = new ArrayList<>();
        for (Tile tile : tiles) {
            if (!isTileAlreadyUsed(previousTiles, tile)) {
                for (Direction direction : Direction.values()) {
                    Tile[] previousTileClone = previousTiles.clone();
                    previousTileClone[pos] = tile.rotateTo(direction);
                    if (SolutionChecker.check(previousTileClone)) {
                        System.out.println("found some match: " +
                            Arrays.toString(previousTileClone));
                        results.addAll(findSolutionsRecursive(previousTileClone));
                    }
                }
            }
        }
        return results;
    }

    private static boolean isTileAlreadyUsed(Tile[] previousTiles, Tile tile) {
        for (Tile previousTile : previousTiles) {
            if (tile.equals(previousTile)) return true;
        }
        return false;
    }

    private static class PuzzleSolverTask extends RecursiveTask<List<Tile[]>> {
        // TODO: See this like an object that embodies the recursive method call
        // TODO: The parameter of findSolutionsRecursive() becomes a field
        // TODO: The body of findSolutionsRecursive() can be copied into
        // TODO: the compute() method below.  Where you before would call the
        // TODO: method recursively, you would now create a new PuzzleSolverTask
        // TODO: and at the end, before returning, you would call
        // TODO: invokeAllAndMerge() with a new ArrayList<>() as identity and
        // TODO: a BinaryOperator that can merge two lists together
        // TODO: Note: For this exercise, we won't use a sequential threshold
        protected List<Tile[]> compute() {
            throw new UnsupportedOperationException("TODO");
        }
    }


    public static <T> T invokeAllAndMerge(BinaryOperator<T> merger,
                                          T identity,
                                          Collection<? extends RecursiveTask<T>> tasks) {
        ForkJoinTask.invokeAll(tasks);
        return tasks.stream()
            .map(ForkJoinTask::join)
            .reduce(identity, merger);
    }
}

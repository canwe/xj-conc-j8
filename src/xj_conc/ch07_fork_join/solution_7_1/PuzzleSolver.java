package xj_conc.ch07_fork_join.solution_7_1;

import xj_conc.ch07_fork_join.exercise_7_1.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

/**
 * In our fork/join solution, we only search to a depth of 2 tiles using
 * additional threads.  Beyond that, we might create too many threads.  Instead,
 * we compute() the solution directly once we are down to a depth of 2.
 */
public class PuzzleSolver {
    private final static Tile[] tiles = TileFactory.makeTiles();

    public List<Tile[]> findSolutions() {
        return new PuzzleSolverTask(new Tile[tiles.length]).invoke();
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
        private final Tile[] previousTiles;

        public PuzzleSolverTask(Tile[] previousTiles) {
            this.previousTiles = previousTiles;
        }

        protected List<Tile[]> compute() {
            int pos = 0;
            for (Tile tile : previousTiles) {
                if (tile == null) break;
                pos++;
            }
            if (pos == 9) {
                return Collections.singletonList(previousTiles);
            }

            List<PuzzleSolverTask> tasks = new ArrayList<>();
            for (Tile tile : tiles) {
                if (!isTileAlreadyUsed(previousTiles, tile)) {
                    for (Direction direction : Direction.values()) {
                        Tile[] previousTileClone = previousTiles.clone();
                        previousTileClone[pos] = tile.rotateTo(direction);
                        if (SolutionChecker.check(previousTileClone)) {
                            System.out.println("found some match: " +
                                Arrays.toString(previousTileClone));
                            tasks.add(new PuzzleSolverTask(previousTileClone));
                        }
                    }
                }
            }
            return invokeAllAndMerge((l1, l2) -> {
                l1.addAll(l2);
                return l1;
            }, new ArrayList<>(), tasks);
        }
    }

    public static <T> T invokeAllAndMerge(BinaryOperator<T> merger,
        T identity, Collection<? extends RecursiveTask<T>> tasks) {
        ForkJoinTask.invokeAll(tasks);
        return tasks.stream()
            .map(ForkJoinTask::join)
            .reduce(identity, merger);
    }
}

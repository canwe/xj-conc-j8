package xj_conc.ch07_fork_join.solution_7_1;

import xj_conc.ch07_fork_join.exercise_7_1.*;

import javax.swing.*;

/**
 * DO NOT CHANGE.
 */
public class PuzzleFrame extends JFrame {
    private final JTabbedPane tabs;

    public PuzzleFrame() {
        PuzzleSolver solver = new PuzzleSolver();
        tabs = new JTabbedPane();
        add(tabs);
        tabs.add("Random", new PuzzleDisplay(3, 3, TileFactory.makeTiles()));
        long time = System.currentTimeMillis();
        java.util.List<Tile[]> tiles = solver.findSolutions();
        time = System.currentTimeMillis() - time;
        System.out.println("time = " + time);
        for (int i = 0; i < tiles.size(); i++) {
            Tile[] tile = tiles.get(i);
            tabs.add("Solution " + (i + 1), new PuzzleDisplay(3, 3, tile));
        }
    }
}

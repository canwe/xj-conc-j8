package xj_conc.ch07_fork_join.solution_7_1;

import javax.swing.*;

/**
 * DO NOT CHANGE.
 */
public class Main {
    public static void main(String... args) {
        SwingUtilities.invokeLater(() -> {
            PuzzleFrame frame = new PuzzleFrame();
            frame.pack();
            frame.setVisible(true);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        });
    }
}

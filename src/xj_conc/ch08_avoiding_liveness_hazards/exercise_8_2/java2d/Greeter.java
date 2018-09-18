package xj_conc.ch08_avoiding_liveness_hazards.exercise_8_2.java2d;

import java.awt.*;

/**
 * Class that updates the global label to greet the user.
 */
public class Greeter {
    public static final Label label = new Label("<empty>");

    static {
        new Greeter();
    }

    private void hello() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                label.setText("Good morning!");
            }
        });
    }

    private Greeter() {
        Thread t = new Thread(new Runnable() {
            public void run() {
                hello();
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void init() {
    }
}
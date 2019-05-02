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
        EventQueue.invokeLater(() -> label.setText("Good morning!"));
    }

    private Greeter() {
        // with just inner anonymous Runnable class
        // it generates new class Greeter$2 passing reference of Greeter.this
        // to it's constructor
        // inside run method it then calls
        // Greeter.access___(Greeter.this /*using stored reference*/)
        // access___ is a static synthetic method in Greeter class,
        // which just calls x0.hello() (x0 is a Greeter.this)
        // it generates this synthetic method to access *private* method hello
        // x0.hello() here is a problem because
        // we don't have Greeter object constructed yet
        // Since the synthetic method is static, it is waiting for the static initializer to finish. However,
        // the static initializer is waiting for the thread to finish, hence causing a deadlock.

        // On the other hand, the lambda version does not rely on an inner class.
        // The bytecode of the constructor relies on an invokedynamic
        Thread t = new Thread(this::hello);
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

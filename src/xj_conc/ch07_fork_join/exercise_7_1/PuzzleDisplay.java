package xj_conc.ch07_fork_join.exercise_7_1;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;

/**
 * DO NOT CHANGE.
 */
public class PuzzleDisplay extends JPanel {
    private final JLabel[] labels = new JLabel[9];

    public PuzzleDisplay(int rows, int cols, Tile[] tiles) {
        setLayout(new GridLayout(rows, cols));
        for (int i = 0; i < labels.length; i++) {
            labels[i] = new JLabel();
            add(labels[i]);
        }
        show(tiles);
    }

    public void show(Tile... tiles) {
        for (int i = 0; i < tiles.length; i++) {
            ImageIcon icon = new ImageIcon(getClass().getResource(tiles[i].getFilename()));
            icon = createRotatedImage(this, icon, tiles[i].getDirection().getDegrees());
            labels[i].setIcon(icon);
        }
    }

    private final static double DEGREE_90 = 90.0 * Math.PI / 180.0;

    public static ImageIcon createRotatedImage(Component c, Icon icon, double rotatedAngle) {
        double originalAngle = rotatedAngle % 360;
        if (rotatedAngle != 0 && originalAngle == 0) {
            originalAngle = 360.0;
        }

        double angle = originalAngle % 90;
        if (originalAngle != 0.0 && angle == 0.0) {
            angle = 90.0;
        }

        double radian = Math.toRadians(angle);

        int iw = icon.getIconWidth();
        int ih = icon.getIconHeight();
        int w;
        int h;

        if ((originalAngle >= 0 && originalAngle <= 90) || (originalAngle > 180 && originalAngle <= 270)) {
            w = (int) (iw * Math.sin(DEGREE_90 - radian) + ih * Math.sin(radian));
            h = (int) (iw * Math.sin(radian) + ih * Math.sin(DEGREE_90 - radian));
        } else {
            w = (int) (ih * Math.sin(DEGREE_90 - radian) + iw * Math.sin(radian));
            h = (int) (ih * Math.sin(radian) + iw * Math.sin(DEGREE_90 - radian));
        }
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        Graphics2D g2d = (Graphics2D) g.create();

        int cx = iw / 2;
        int cy = ih / 2;

        g2d.translate(w / 2, h / 2);

        g2d.rotate(Math.toRadians(originalAngle));

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        icon.paintIcon(c, g2d, -cx, -cy);

        g2d.dispose();
        return new ImageIcon(image);
    }

}

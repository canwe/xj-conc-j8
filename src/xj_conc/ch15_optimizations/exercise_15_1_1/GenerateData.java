package xj_conc.ch15_optimizations.exercise_15_1_1;

import java.io.*;
import java.util.*;

public class GenerateData {
    public static final int ROWS_IN_DATASET = 1000 * 1000;
    public static final String DATASET1 = "dataset1.dat";
    public static final String DATASET2 = "dataset2.dat";
    public static final String DATASET3 = "dataset3.dat";

    public static void main(String... args) throws IOException {
        if (!new File(DATASET1).exists()) generateDataset1();
        if (!new File(DATASET2).exists()) generateDataset2();
        if (!new File(DATASET3).exists()) generateDataset3();
    }

    // dataset 1, all true, integers 2 to 5 characters long starting with '3'
    public static void generateDataset1() throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(
            new FileOutputStream(DATASET1)));
        Random rand = new Random(12346);
        for (int i = 0; i < ROWS_IN_DATASET; i++) {
            out.writeObject("3" + Math.abs(rand.nextInt(10000)));
            out.reset();
        }
        out.writeObject(null);
        out.close();
    }

    // dataset 2, about half true
    public static void generateDataset2() throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(
            new FileOutputStream(DATASET2)));
        Random rand = new Random(12347);
        for (int j = 0; j < ROWS_IN_DATASET / 10; j++) {
            out.writeObject("3" + Math.abs(rand.nextInt(10000)));
            out.writeObject("-" + Math.abs(rand.nextInt(10000)));
            out.writeObject("3" + Math.abs(rand.nextInt(10000)));
            int i = Math.abs(rand.nextInt(100000));
            while (Integer.toString(i).charAt(0) == '3') {
                i = Math.abs(rand.nextInt(100000));
            }
            out.writeObject("" + i);
            out.writeObject("3" + Math.abs(rand.nextInt(10000)));
            out.writeObject("-" + Math.abs(rand.nextInt(10000)));
            out.writeObject("3" + Math.abs(rand.nextInt(10000)));
            i = Math.abs(rand.nextInt(100000));
            while (Integer.toString(i).charAt(0) == '3') {
                i = Math.abs(rand.nextInt(100000));
            }
            out.writeObject("" + i);
            out.writeObject("3" + Math.abs(rand.nextInt(10000)));
            out.writeObject("-" + Math.abs(rand.nextInt(10000)));
            out.reset();
        }
        out.writeObject(null);
        out.close();
    }

    // dataset 2, about 1/3 true, 1/3 false, over 1/3 not a number
    public static void generateDataset3() throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(
            new FileOutputStream(DATASET3)));
        Random rand = new Random(12348);
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < ROWS_IN_DATASET / 10; j++) {
            out.writeObject("3" + Math.abs(rand.nextInt(10000)));
            out.writeObject("-" + Math.abs(rand.nextInt(10000)));
            sb.setLength(0);
            sb.append(rand.nextInt(1000));
            sb.insert(0, 'w');
            out.writeObject(sb.toString());
            out.writeObject("3" + Math.abs(rand.nextInt(10000)));
            int i = Math.abs(rand.nextInt(100000));
            while (Integer.toString(i).charAt(0) == '3') {
                i = Math.abs(rand.nextInt(100000));
            }
            out.writeObject("" + i);
            sb.setLength(0);
            sb.append(rand.nextInt(1000));
            sb.insert(1, 'q');
            out.writeObject(sb.toString());
            out.writeObject("3" + Math.abs(rand.nextInt(10000)));
            out.writeObject("-" + Math.abs(rand.nextInt(10000)));
            sb.setLength(0);
            sb.append(rand.nextInt(1000));
            sb.append('s');
            out.writeObject(sb.toString());
            out.writeObject("NOTNUM");
            out.reset();
        }
        out.writeObject(null);
        out.close();
    }
}

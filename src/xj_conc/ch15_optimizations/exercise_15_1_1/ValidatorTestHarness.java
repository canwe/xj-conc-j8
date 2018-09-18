package xj_conc.ch15_optimizations.exercise_15_1_1;

import xj_conc.util.*;

import java.io.*;
import java.util.*;

public class ValidatorTestHarness {
    public static final int REPEATS = 1;
    public static final int DATA_POINTS = 10;

    private static final Map<Class<? extends Validator>, Map<String, Long>> bestTimes =
        new LinkedHashMap<>();

    public static void main(String... args) throws IOException,
        ClassNotFoundException {
        SuperSimpleGCParser.showGCLogSummaryAtExit();
        GenerateData.main(args);
        Validator[] validators = {
            new ValidatorImpl(),
        };
        // CorrectnessTest.check(validators);
        for (Validator val : validators) {
            System.out.println(val.getClass().getSimpleName());
            System.out.println(val.getClass().getSimpleName().replaceAll(".", "="));
            System.out.println("Dataset1 Count\tDataset1 Time\tDataset2 Count"
                + "\tDataset2 Time\tDataset3 Count\tDataset3 Time");
            for (int i = 0; i < DATA_POINTS; i++) {
                testAllDatasets(val);
            }
        }
        Map<String, Long> initialTimes = bestTimes.get(ValidatorImpl.class);
        for (Map.Entry<Class<? extends Validator>, Map<String, Long>> entry : bestTimes.entrySet()) {
            System.out.println(entry.getKey().getSimpleName());
            for (Map.Entry<String, Long> result : entry.getValue().entrySet()) {
                System.out.printf("\t%s %.2fx%n", result.getKey(), (double) initialTimes.get(result.getKey()) / result.getValue());
            }
        }
    }

    private static void testAllDatasets(Validator val) throws IOException,
        ClassNotFoundException {
        test(val, GenerateData.DATASET1);
        System.out.print("\t");
        System.out.flush();
        test(val, GenerateData.DATASET2);
        System.out.print("\t");
        System.out.flush();
        test(val, GenerateData.DATASET3);
        System.out.println();
    }

    private final static Benchmark mbm = new Benchmark();

    private static void test(Validator val, String dataset) throws IOException,
        ClassNotFoundException {
        mbm.start();

        int truecount = 0;
        for (int i = 0; i < REPEATS; i++) {
            try (
                FileInputStream fis = new FileInputStream(dataset);
                ObjectInputStream in = new ObjectInputStream(fis)
            ) {
                truecount = 0;
                String s;
                while ((s = (String) in.readObject()) != null) {
                    if (val.checkInteger(s))
                        truecount++;
                }
            }
        }

        mbm.stop();
        System.out.printf("%d\t%s", truecount, mbm);

        Map<String, Long> time = new TreeMap<>();
        time.put(dataset, mbm.getUserTime());
        bestTimes.merge(val.getClass(), time, (oldTime, newTime) -> {
            oldTime.merge(dataset, mbm.getUserTime(), Long::min);
            return oldTime;
        });
    }
}

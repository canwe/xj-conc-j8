package xj_conc.util;

import java.io.*;
import java.lang.management.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import java.util.stream.*;

/**
 * This is meant to be an extremely simple GC parser, only to be used for our
 * exercises.  For more serious analysis, we recommend JClarity's Censum or
 * HP JMeter (not Apache JMeter, that's something else).
 */
public class SuperSimpleGCParser {
    public static void main(String... args) {
        if (args.length == 0) {
            System.err.println("Usage: java SuperSimpleGCParser " +
                "filename1 filename2 ...\n" +
                "\tGC Log should be generated with just -Xloggc:filename");
            System.exit(1);
        }

        System.out.println("Disclaimer: This is not a robust GC viewer.  " +
            "Please use JClarity Censum for serious analysis.");
        Stream.of(args)
            .map(GCStatistics::new)
            .forEach(System.out::println);
    }

    public static void showGCLogSummaryAtExit() {
        Optional<String> xloggc = VMArgsChecker.findVMArg("-Xloggc:");
        String gclog = xloggc.orElseThrow(() ->
            new AssertionError("Please use -Xloggc:filename.vgc VM argument!"))
            .substring("-Xloggc:".length());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> main(gclog)));
    }

    private enum Type {YOUNG, OLD, UNDEFINED}

    private static class Event {
        private final String line;
        private final Type type;
        private final long timestampInMilliseconds;
        private final long memoryBefore;
        private final long memoryAfter;
        private final double durationInSeconds;


        public Event(String line) {
            this.line = line;
            type = extractType(line);
            timestampInMilliseconds = extractTimeStamp(line);
            memoryBefore = extractMemoryBefore(line);
            memoryAfter = extractMemoryAfter(line);
            durationInSeconds = extractDuration(line);
        }

        public boolean isOld() {
            return type == Type.OLD;
        }

        public boolean isYoung() {
            return type == Type.YOUNG;
        }

        public long getMemoryReclaimed() {
            return memoryBefore - memoryAfter;
        }

        public double getTimeInGC() {
            return durationInSeconds;
        }

        public long getHeapAfterGC() {
            return memoryAfter;
        }
    }

    private static Type extractType(String line) {
        if (line.contains("[GC")) {
            return Type.YOUNG;
        } else if (line.contains("[Full GC")) return Type.OLD;
        else return Type.UNDEFINED;
    }

    private static long extractTimeStamp(String line) {
        line = line.substring(0, line.indexOf(':'));
        line = line.replaceAll("\\.", "");
        line = line.replaceAll("\\,", "");
        line = line.replaceAll("^0*", "");
        return Long.parseLong(line);
    }

    private static long extractMemoryBefore(String line) {
        line = line.replaceAll("K->.*", "");
        line = line.replaceAll(".* ", "");
        return Long.parseLong(line);
    }

    private static long extractMemoryAfter(String line) {
        line = line.replaceAll(".*K->", "");
        line = line.replaceAll("K.*", "");
        return Long.parseLong(line);
    }

    private static double extractDuration(String line) {
        line = line.replaceAll(" secs]", "");
        line = line.replaceAll(".* ", "");
        line = line.replace(',', '.');
        return Double.parseDouble(line);
    }

    private static class GCStatistics {
        private final String filename;
        private final long durationOfLog;
        private final int numberOfGCs;
        private final int numberOfYoungGCs;
        private final int numberOfOldGCs;
        private final int numberOfUndefinedGCs;
        private final long memoryReclaimedDuringYoung;
        private final long memoryReclaimedDuringOld;
        private final double timeInGCs;
        private final double timeInYoungGCs;
        private final double timeInOldGCs;
        private final long maxHeapAfterGC;
        private final long totalMemoryAllocated;
        private final double averageCreationRate;
        private final double percentageOfTimeInGC;
        private final DoubleSummaryStatistics averateTimeInYoungGCs;

        public GCStatistics(String filename) {
            try (
                Stream<String> lines = Files.lines(Paths.get(filename))
            ){
                List<Event> events =
                    lines
                    .collect(Collectors.toList())
                    .parallelStream()
                    .filter(line -> line.endsWith(" secs]"))
                    .filter(line -> line.contains("K->"))
                    .map(Event::new).collect(Collectors.toList());
                this.filename = filename;
                if (events.isEmpty()) {
                    durationOfLog = 0;
                    numberOfGCs = 0;
                    numberOfYoungGCs = 0;
                    numberOfOldGCs = 0;
                    numberOfUndefinedGCs = 0;
                    memoryReclaimedDuringYoung = 0;
                    memoryReclaimedDuringOld = 0;
                    timeInGCs = 0;
                    timeInYoungGCs = 0;
                    timeInOldGCs = 0;
                    maxHeapAfterGC = 0;
                    totalMemoryAllocated = 0;
                    averageCreationRate = 0;
                    percentageOfTimeInGC = 0;
                    averateTimeInYoungGCs = new DoubleSummaryStatistics();
                } else {
                    Event lastEvent = events.get(events.size() - 1);
                    durationOfLog = (long) (lastEvent.timestampInMilliseconds + lastEvent.durationInSeconds * 1000);
                    numberOfGCs = events.size();
                    numberOfYoungGCs = (int) events.stream().filter(Event::isYoung).count();
                    numberOfOldGCs = (int) events.stream().filter(Event::isOld).count();
                    numberOfUndefinedGCs = numberOfGCs - numberOfYoungGCs - numberOfOldGCs;
                    memoryReclaimedDuringYoung = events.stream().filter(Event::isYoung).mapToLong(Event::getMemoryReclaimed).sum();
                    memoryReclaimedDuringOld = events.stream().filter(Event::isOld).mapToLong(Event::getMemoryReclaimed).sum();
                    timeInGCs = events.stream().mapToDouble(Event::getTimeInGC).sum();
                    timeInYoungGCs = events.stream().filter(Event::isYoung).mapToDouble(Event::getTimeInGC).sum();
                    averateTimeInYoungGCs = events.stream().filter(Event::isYoung).mapToDouble(Event::getTimeInGC).summaryStatistics();
                    timeInOldGCs = events.stream().filter(Event::isOld).mapToDouble(Event::getTimeInGC).sum();
                    maxHeapAfterGC = events.stream().mapToLong(Event::getHeapAfterGC).max().orElseThrow(AssertionError::new);
                    totalMemoryAllocated = events.stream().mapToLong(Event::getMemoryReclaimed).sum() + lastEvent.getHeapAfterGC();
                    averageCreationRate = totalMemoryAllocated * 1000 / durationOfLog;
                    percentageOfTimeInGC = timeInGCs * 100000 / durationOfLog;
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        public String toString() {
            return "GCStatistics for " + filename +
                "\n\tdurationOfLog=" + Duration.ofMillis(durationOfLog) +
                "\n\tnumberOfGCs=" + numberOfGCs +
                "\n\tnumberOfYoungGCs=" + numberOfYoungGCs +
                "\n\tnumberOfOldGCs=" + numberOfOldGCs +
                (numberOfUndefinedGCs > 0 ? "\n\tnumberOfUndefinedGCs=" + numberOfUndefinedGCs : "") +
                "\n\tmemoryReclaimedDuringYoung=" + Memory.format(memoryReclaimedDuringYoung, Memory.KILOBYTES, 3) +
                "\n\tmemoryReclaimedDuringOld=" + Memory.format(memoryReclaimedDuringOld, Memory.KILOBYTES, 3) +
                "\n\tmaxHeapAfterGC=" + Memory.format(maxHeapAfterGC, Memory.KILOBYTES, 3) +
                "\n\ttotalMemoryAllocated=" + Memory.format(totalMemoryAllocated, Memory.KILOBYTES, 3) +
                "\n\taverageCreationRate=" + Memory.format(averageCreationRate, Memory.KILOBYTES, 2) + "/s" +
                "\n\ttimeInGCs=" + Duration.ofNanos((long) (timeInGCs * 1_000_000_000)) +
                "\n\ttimeInYoungGCs=" + Duration.ofNanos((long) (timeInYoungGCs * 1_000_000_000)) +
                "\n\taverageTimeInYoungGCs=" + averateTimeInYoungGCs +
                "\n\ttimeInOldGCs=" + Duration.ofNanos((long) (timeInOldGCs * 1_000_000_000)) +
                "\n\tpercentageOfTimeInGC=" + String.format("%.2f%%", percentageOfTimeInGC);
        }
    }
}

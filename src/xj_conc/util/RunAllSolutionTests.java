package xj_conc.util;

import junit.framework.*;
import junit.textui.*;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;

public class RunAllSolutionTests {
    public static void main(String... args) throws IOException {
        Files.walkFileTree(Paths.get("src"), new SimpleFileVisitor<Path>() {
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                String name = file.toString();
                if (name.contains("solution") && containsTest(file) && name.matches(".*/ch.*")) {
                    runTest(file);
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private static boolean containsTest(Path file) throws IOException {
        return Files.lines(file)
            .filter(line -> line.matches("[ \t]*@Test.*"))
            .findAny()
            .isPresent();
    }

    private static void runTest(Path file) throws IOException {
        try {
            String className = file.toString().substring(4);
            className = className.substring(0, className.length() - 5);
            className = className.replace("/", ".");
            Class<?> clazz = Class.forName(className);
            System.out.println("Running test for " + clazz);
            TestResult result = TestRunner.run(new JUnit4TestAdapter(clazz));
            System.out.println("result.errorCount() = " + result.errorCount());
            System.out.println("result.failureCount() = " + result.failureCount());
            if (result.failureCount() > 0) {
                System.err.println("Oh no!!!");
                System.exit(1);
            }
        } catch (ClassNotFoundException e) {
            throw new IOException(e);
        }
    }
}

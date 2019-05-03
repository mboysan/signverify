package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class TestUtils {

    public final static String TEST_RESOURCE_BASE_PATH = "src/test/resources/";
    private final static Random rng;
    static {
        long seed = System.currentTimeMillis();
        System.out.println("SEED FOR RANDOM: " + seed);
//        rng = new Random(seed);
        rng = new Random(1556895844708L);
    }

    public static Random getRng() {
        return rng;
    }

    public static List<String> createEvents(int size, String eventStrPrefix) {
        return Stream.iterate(0, i -> i + 1)
                .limit(size)
                .map(i -> eventStrPrefix + i)
                .collect(Collectors.toList());
    }

    public static String eventsAsLines(List<String> events) {
        return events.stream().reduce((s, s2) -> s + String.format("%n") + s2).get();
    }

    @SafeVarargs
    public static List<String> flatten(List<String>... events) {
        return Stream.of(events).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public static void createFile(String logPath, String content) throws IOException {
        Files.write(Paths.get(logPath), content.getBytes());
    }

    public static void appendToFile(String logPath, String content) throws IOException {
        Files.write(Paths.get(logPath), content.getBytes(), StandardOpenOption.APPEND);
    }


}

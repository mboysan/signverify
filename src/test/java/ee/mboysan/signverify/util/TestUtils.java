package ee.mboysan.signverify.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class TestUtils {

    public final static String TEST_RESOURCE_BASE_PATH = "src/test/resources/";
    private final static Random rng;
    static {
        long seed = System.currentTimeMillis();
        System.out.println("SEED FOR RANDOM: " + seed);
        rng = new Random(seed);
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

    public static List<String> createRandomFile(String logPath, int minSize, int maxSize) throws IOException {
        if (minSize <= 0 || maxSize < minSize) {
            throw new IllegalArgumentException("min & max size have to be set properly");
        }
        int totalEvents = getRng().nextInt((maxSize - minSize) + 1) + minSize;
        List<String> contentList = Stream.generate(() -> UUID.randomUUID().toString())
                .limit(totalEvents)
                .collect(Collectors.toList());
        String content = eventsAsLines(contentList);
        createFile(logPath, content);
        return contentList;
    }

    public static void createFile(String logPath, String content) throws IOException {
        Files.write(Paths.get(logPath), content.getBytes());
    }

    public static void appendToFile(String logPath, String content) throws IOException {
        Files.write(Paths.get(logPath), (String.format("%n") + content).getBytes(), StandardOpenOption.APPEND);
    }

    public static void alterFileDeleteRandomLine(String logPath) throws IOException {
        File file = new File(logPath);
        List<String> lines = Files.readAllLines(file.toPath());
        Files.delete(file.toPath());
        lines.remove(getRng().nextInt(lines.size()));
        Files.write(file.toPath(), eventsAsLines(lines).getBytes());
    }

    public static void alterFileChangeRandomLine(String logPath) throws IOException {
        File file = new File(logPath);
        List<String> lines = Files.readAllLines(file.toPath());
        Files.delete(file.toPath());
        int lineToChange = getRng().nextInt(lines.size());
        AtomicInteger currLine = new AtomicInteger();
        lines = lines.stream().map(s -> {
            if (currLine.getAndIncrement() == lineToChange) {
                return s + "new change";
            }
            return s;
        }).collect(Collectors.toList());
        Files.write(file.toPath(), eventsAsLines(lines).getBytes());
    }


}

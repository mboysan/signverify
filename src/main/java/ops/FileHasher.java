package ops;

import exceptions.FileHashingFailedException;
import hashing.IHash;
import tree.HashTree;
import tree.HashTreeAggregator;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class FileHasher {

    static int CHUNK_SIZE = 4;
    private final HashTree fileHashTree;

    public FileHasher(File file) throws Exception {
        this(file.getPath());
    }

    FileHasher(String pathToFile) throws Exception {
        fileHashTree = hashFile(pathToFile);
    }

    private HashTree hashFile(String pathToFile) throws Exception {
        try(Stream<String> lines = Files.lines(Paths.get(pathToFile));
            HashTreeAggregator hashTreeAggregator = new HashTreeAggregator()) {

            EventCollector collector = new EventCollector(CHUNK_SIZE);
            AtomicInteger lineN = new AtomicInteger();
            lines.forEach(line -> {
                collector.append(line);
                if (collector.canCollect()) {
                    hashTreeAggregator.aggregateEvents(collector.collectAndReset());
                }
                lineN.getAndIncrement();
            });
            if (collector.hasRemaining()) {
                hashTreeAggregator.aggregateEvents(collector.collectAndReset());
            }
            if (lineN.get() == 0) {
                throw new FileHashingFailedException("Cannot hash a file with empty content");
            }
            return hashTreeAggregator.endAggregation().getAggregatedTree();
        }
    }

    private static class EventCollector {
        List<String> lines = new ArrayList<>();
        boolean canCollect = false;
        final int chunkSize;
        EventCollector(int chunkSize) {
            this.chunkSize = chunkSize;
        }
        void append(String event) {
            lines.add(event);
            canCollect = lines.size() >= chunkSize;
        }
        boolean canCollect() {
            return canCollect;
        }
        boolean hasRemaining() {
            return lines.size() > 0 && lines.size() <= chunkSize;
        }
        List<String> collectAndReset() {
            List<String> toCollect = lines;
            lines = new ArrayList<>();
            canCollect = false;
            return toCollect;
        }
    }

    public IHash getFileHash() {
        return getFileHashTree().getRoot().getHash();
    }

    public HashTree getFileHashTree() {
        return fileHashTree;
    }
}

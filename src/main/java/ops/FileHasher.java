package ops;

import exceptions.FileHashingFailedException;
import hashing.IHash;
import tree.HashTree;
import tree.HashTreeAggregator;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class FileHasher {

    static int CHUNK_SIZE = 4;
    private final HashTree fileHashTree;
    private final int prevEventCount;

    public FileHasher(File file) throws Exception {
        this(file, -1);
    }

    public FileHasher(File file, int prevEventCount) throws Exception {
        this.prevEventCount = prevEventCount;
        fileHashTree = hashFile(file);
    }

    private HashTree hashFile(File file) throws Exception {
        try(Stream<String> lines = Files.lines(file.toPath());
            HashTreeAggregator hashTreeAggregator = new HashTreeAggregator()) {

            EventCollector collector = new EventCollector(CHUNK_SIZE);
            AtomicInteger lineN = new AtomicInteger(0);
            AtomicReference<Exception> catchedEx = new AtomicReference<>();
            lines.forEach(line -> {
                try {
                    collector.append(line);
                    if (lineN.incrementAndGet() == prevEventCount) {
                        /* aggregate the events remaining dangling formed from the previous file.
                           and the current aggregation and continue.
                         */
                        hashTreeAggregator.aggregateEvents(collector.collectAndReset());
                        hashTreeAggregator.endAggregation();
                    }
                    if (collector.canCollect()) {
                        hashTreeAggregator.aggregateEvents(collector.collectAndReset());
                    }
                } catch (Exception e) {
                    catchedEx.set(e);
                }
            });
            if (catchedEx.get() != null) {
                throw new FileHashingFailedException(catchedEx.get());
            }
            if (lineN.get() == 0) {
                throw new FileHashingFailedException("Cannot hash a file with empty content");
            }
            if (collector.hasRemaining()) {
                hashTreeAggregator.aggregateEvents(collector.collectAndReset());
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

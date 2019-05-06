package ee.mboysan.signverify.ops;

import ee.mboysan.signverify.exceptions.FileHashingFailedException;
import ee.mboysan.signverify.hashing.HashUtils;
import ee.mboysan.signverify.hashing.IHash;
import ee.mboysan.signverify.tree.HashTree;
import ee.mboysan.signverify.tree.HashTreeAggregator;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class FileHasher {

    /**
     * Default number of lines to read as chunks.
     */
    static int CHUNK_SIZE = 256;
    /**
     * The hash tree built.
     */
    private final HashTree fileHashTree;
    /**
     * When appending to log files (append-only logs) are allowed, we need to take the previous line count of the
     * file to create a consistent hash tree.
     */
    private final int prevEventCount;

    /**
     * @see FileHasher#FileHasher(File, int, String)
     */
    FileHasher(File file) throws Exception {
        this(file, HashUtils.getDefaultHashAlgorithm());
    }

    /**
     * @see FileHasher#FileHasher(File, int, String)
     */
    public FileHasher(File file, String hashAlgorithm) throws Exception {
        this(file, -1, hashAlgorithm);
    }

    /**
     * @param file file to create a hash tree.
     * @param prevEventCount see {@link #prevEventCount}.
     * @param hashAlgorithm hash algorithm to use for building the hash tree.
     * @throws Exception if a problem occurs when creating a hash tree for the file.
     */
    public FileHasher(File file, int prevEventCount, String hashAlgorithm) throws Exception {
        this.prevEventCount = prevEventCount;
        fileHashTree = hashFile(file, hashAlgorithm);
    }

    /**
     * creates a hash tree from the given file and hash algorithm
     */
    private HashTree hashFile(File file, String hashAlgorithm) throws Exception {
        try(Stream<String> lines = Files.lines(file.toPath());
            HashTreeAggregator hta = new HashTreeAggregator(hashAlgorithm)) {

            EventCollector collector = new EventCollector(CHUNK_SIZE);
            AtomicInteger lineN = new AtomicInteger(0);
            lines.forEach(line -> {
                try {
                    collector.append(line);
                    if (lineN.incrementAndGet() == prevEventCount) {
                        /* aggregate the events remaining dangling formed from the previous file
                           and the current aggregation and continue. */
                        hta.aggregateEvents(collector.collectAndReset());
                        hta.endAggregation();
                    }
                    if (collector.canCollect()) {
                        hta.aggregateEvents(collector.collectAndReset());
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            if (lineN.get() == 0) {
                throw new FileHashingFailedException("Cannot hash a file with empty content");
            }
            if (collector.hasRemaining()) {
                hta.aggregateEvents(collector.collectAndReset());
            }
            return hta.endAggregation().getAggregatedTree();
        }
    }

    /**
     * A helper class for collecting events based on the {@link #CHUNK_SIZE}.
     */
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

    /**
     * @return hash obj of the file.
     */
    public IHash getFileHash() {
        return getFileHashTree().getRoot().getHash();
    }

    /**
     * @return {@link #fileHashTree}.
     */
    public HashTree getFileHashTree() {
        return fileHashTree;
    }
}

package ops;

import hashing.IHash;
import tree.HashTreeAggregator;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class FileHasher {

    private static final int DEFAULT_CHUNK_SIZE = 4;

    private final IHash fileHash;

    public FileHasher(File file) throws Exception {
        this(file.getPath());
    }

    public FileHasher(String pathToFile) throws Exception {
        this(pathToFile, DEFAULT_CHUNK_SIZE);
    }

    FileHasher(String pathToFile, int chunksToRead) throws Exception {
        fileHash = hashFile(pathToFile, chunksToRead);
    }

    private IHash hashFile(String pathToFile, int chunksToRead) throws Exception {
        try(Stream<String> lines = Files.lines(Paths.get(pathToFile));
            HashTreeAggregator hashTreeAggregator = new HashTreeAggregator()) {

            AtomicInteger numLinesRead = new AtomicInteger();
            List<String> events = new ArrayList<>();

            lines.forEach(line -> {
                events.add(line);
                if (numLinesRead.incrementAndGet() == chunksToRead) {
                    hashTreeAggregator.aggregateEvents(new ArrayList<>(events));
                    events.clear();
                    numLinesRead.set(0);
                }
            });
            if (events.size() > 0) {
                // aggregate the remaining events
                hashTreeAggregator.aggregateEvents(new ArrayList<>(events));
            }
            return hashTreeAggregator.endAndGetRootHash();
        }
    }

    public IHash getFileHash() {
        return this.fileHash;
    }

}

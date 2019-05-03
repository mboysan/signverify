package ops;

import exceptions.FileHashingFailedException;
import hashing.IHash;
import org.junit.Ignore;
import org.junit.Test;
import tree.HashTreeAggregator;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static util.TestUtils.*;

public class FileHasherTest {

    @Test
    public void testRandomFileHashingWithUniqueContent() throws Exception {
        FileHasher.CHUNK_SIZE = 4;
        int totalEvents = getRng().nextInt(500) + 1;
        List<String> contentList = Stream.generate(() -> UUID.randomUUID().toString())
                .limit(totalEvents)
                .collect(Collectors.toList());
        String content = eventsAsLines(contentList);

        File file = new File("src/test/resources/tmp.log");
        try {
            createFile(file.getPath(), content);

            FileHasher fileHasher = new FileHasher(file);
            IHash actualHash = fileHasher.getFileHash();

            try(HashTreeAggregator aggr = new HashTreeAggregator()) {
                List<String> toAggregate = new ArrayList<>();
                for (int i = 0, l = 1; i < contentList.size(); i++, l++) {
                    toAggregate.add(contentList.get(i));
                    if(l == FileHasher.CHUNK_SIZE) {
                        aggr.aggregateEvents(new ArrayList<>(toAggregate));
                        toAggregate.clear();
                        l = 0;
                    }
                }
                if (toAggregate.size() > 0) {
                    aggr.aggregateEvents(new ArrayList<>(toAggregate));
                }
                IHash expectedHash = aggr.endAggregation().getAggregatedTree().getRoot().getHash();

                assertEquals(expectedHash, actualHash);
            }
        } finally {
            Files.delete(file.toPath());
        }
    }

    @Test(expected = FileHashingFailedException.class)
    public void testFileHashingEmptyFile() throws Exception {
        File file = new File("src/test/resources/empty.txt");
        new FileHasher(file.getPath());
    }

    @Test
    public void testFileHashingEmptyLines() throws Exception {
        File file = new File("src/test/resources/twoemptylines.txt");
        FileHasher fileHasher = new FileHasher(file.getPath());
        IHash actualHash = fileHasher.getFileHash();

        try(HashTreeAggregator aggr = new HashTreeAggregator()) {
            List<String> events = Stream.of("","").collect(Collectors.toList());
            aggr.aggregateEvents(events);

            IHash expectedHash = aggr.endAggregation().getAggregatedTree().getRoot().getHash();

            assertEquals(expectedHash, actualHash);
        }
    }

    @Ignore
    @Test
    public void testFileHashingLineAtEndAndNoLineAtEnd() throws Exception {
        File lineAtEnd = new File("src/test/resources/lineatend.txt");
        File noLineAtEnd = new File("src/test/resources/nolineatend.txt");

        FileHasher fileHasher1 = new FileHasher(lineAtEnd.getPath());
        IHash hash1 = fileHasher1.getFileHash();

        FileHasher fileHasher2 = new FileHasher(noLineAtEnd.getPath());
        IHash hash2 = fileHasher2.getFileHash();

        assertNotEquals(hash1, hash2);
    }

}
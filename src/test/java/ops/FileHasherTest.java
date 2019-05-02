package ops;

import exceptions.TreeConstructionFailedException;
import hashing.IHash;
import org.junit.Test;
import tree.HashTreeAggregator;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class FileHasherTest {

    @Test
    public void testFileHashingRegular() throws Exception {
        File file = new File("src/test/resources/filehasher_test.txt");
        FileHasher fileHasher = new FileHasher(file.getPath(), 4);
        IHash actualHash = fileHasher.getFileHash();

        List<String> events1 = Stream.of(
                "event1 -- chunk 0",
                "event2",
                "event3",
                "event4"
        ).collect(Collectors.toList());
        List<String> events2 = Stream.of(
                "event5 -- chunk 1",
                "event6",
                "event7",
                "event8"
        ).collect(Collectors.toList());
        List<String> events3 = Stream.of(
                "event9 -- chunk 2",
                "event10"
        ).collect(Collectors.toList());
        IHash expectedHash = new HashTreeAggregator()
                .aggregateEvents(events1)
                .aggregateEvents(events2)
                .aggregateEvents(events3)
                .endAndGetRootHash();

        assertEquals(expectedHash, actualHash);
    }

    @Test
    public void testFileHashingLargeChunk() throws Exception {
        File file = new File("src/test/resources/filehasher_test.txt");
        FileHasher fileHasher = new FileHasher(file.getPath(), 10);
        IHash actualHash = fileHasher.getFileHash();

        List<String> events1 = Stream.of(
                "event1 -- chunk 0",
                "event2",
                "event3",
                "event4",
                "event5 -- chunk 1",
                "event6",
                "event7",
                "event8",
                "event9 -- chunk 2",
                "event10"
        ).collect(Collectors.toList());
        IHash expectedHash = new HashTreeAggregator()
                .aggregateEvents(events1)
                .endAndGetRootHash();

        assertEquals(expectedHash, actualHash);
    }

    @Test(expected = TreeConstructionFailedException.class)
    public void testFileHashingEmptyFile() throws Exception {
        File file = new File("src/test/resources/empty.txt");
        FileHasher fileHasher = new FileHasher(file.getPath());
    }

}
package ee.mboysan.signverify.ops;

import ee.mboysan.signverify.exceptions.FileHashingFailedException;
import ee.mboysan.signverify.hashing.IHash;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import ee.mboysan.signverify.tree.HashTreeAggregator;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ee.mboysan.signverify.util.TestUtils.getRng;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static ee.mboysan.signverify.util.TestUtils.createRandomFile;

@RunWith(Parameterized.class)
public class FileHasherTest {

    private final static int MAX_LINES = 500;

    @Parameterized.Parameters(name = "{index}: chunkSize={0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {0}, {1}, {4}, {127}, {MAX_LINES}, {getRng().nextInt(MAX_LINES)}
        });
    }

    @Parameterized.Parameter(0)
    public int chunkSize;

    @Before
    public void setUp() throws Exception {
        FileHasher.CHUNK_SIZE = chunkSize;
    }

    @Test
    public void testRandomFileHashingWithUniqueContent() throws Exception {
        File file = new File("src/test/resources/tmp.log");
        try {
            List<String> contentList = createRandomFile(file.getPath(), 1, 500);

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
        new FileHasher(file);
    }

    @Test
    public void testFileHashingEmptyLines() throws Exception {
        File file = new File("src/test/resources/twoemptylines.txt");
        FileHasher fileHasher = new FileHasher(file);
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

        FileHasher fileHasher1 = new FileHasher(lineAtEnd);
        IHash hash1 = fileHasher1.getFileHash();

        FileHasher fileHasher2 = new FileHasher(noLineAtEnd);
        IHash hash2 = fileHasher2.getFileHash();

        assertNotEquals(hash1, hash2);
    }

}
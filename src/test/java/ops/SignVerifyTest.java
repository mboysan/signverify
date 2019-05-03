package ops;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;

import java.nio.file.Files;
import java.nio.file.Paths;

import static util.TestUtils.*;

@Ignore
public class SignVerifyTest {

    private final static String LOG_FILE = "src/test/resources/tmp.log";

    @Before
    public void setUp() throws Exception {
        createFile(LOG_FILE, eventsAsLines(createEvents(100, "event")));
    }

    @After
    public void tearDown() throws Exception {
        Files.delete(Paths.get(LOG_FILE));
    }
}
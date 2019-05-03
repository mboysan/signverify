package ops;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static util.TestUtils.*;

@Ignore
public class SignVerifyTest {

    private List<File> filesToDelete = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        filesToDelete = new ArrayList<>();
    }

    @After
    public void tearDown() throws Exception {
        filesToDelete.forEach((f) -> {
            try {
                Files.delete(f.toPath());
            } catch (IOException e) {
                e.printStackTrace();    // log the error but do nothing
            }
        });
    }

    @Test
    public void testSignAndVerifyRandomFileAppendNotAllowed() throws Exception {
        File fileToSign = new File("src/test/resources/test.log");
        File signatureFile = new File("src/test/resources/test.sig");
        filesToDelete.add(fileToSign);
        filesToDelete.add(signatureFile);

        createRandomFile(fileToSign.getPath(), 1, 100);

        SignVerify sv = new SignVerify(false);

        Signature signature = sv.sign(fileToSign, signatureFile);

        appendToFile(fileToSign.getPath(), eventsAsLines(createEvents(10, "newEvent")));

        boolean verif = sv.verify(signatureFile, fileToSign);
        assertFalse(verif);
    }

    @Test
    public void testSignAndVerifyRandomFileAppendAllowed() throws Exception {
        File fileToSign = new File("src/test/resources/test.log");
        File signatureFile = new File("src/test/resources/test.sig");
        filesToDelete.add(fileToSign);
        filesToDelete.add(signatureFile);

        createRandomFile(fileToSign.getPath(), 1, 500);

        SignVerify sv = new SignVerify(true);

        Signature signature = sv.sign(fileToSign, signatureFile);

        appendToFile(fileToSign.getPath(), eventsAsLines(createEvents(127, "newEventSet1.")));
        assertTrue(sv.verify(signatureFile, fileToSign));

        appendToFile(fileToSign.getPath(), eventsAsLines(createEvents(127, "newEventSet2.")));
        assertTrue(sv.verify(signatureFile, fileToSign));
    }


}
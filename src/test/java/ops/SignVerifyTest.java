package ops;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import tree.HashTree;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static util.TestUtils.*;

@RunWith(Parameterized.class)
public class SignVerifyTest {

    @Parameterized.Parameters(name = "{index}: hashAlg={0}, opMode={1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {"SHA-256", "MEM"}, {"SHA-1", "MEM"}, {"MD5", "MEM"},
                {"SHA-256", "CPU"}, {"SHA-1", "CPU"}, {"MD5", "CPU"},
        });
    }

    private List<File> filesToDelete = new ArrayList<>();
    private File fileToSign = new File("src/test/resources/test.log");
    private File signatureFile = new File("src/test/resources/test.sig");

    @Parameterized.Parameter(0)
    public String hashAlgorithm;

    @Parameterized.Parameter(1)
    public String opMode;

    @Before
    public void setUp() throws Exception {
        HashTree.OPERATION_MODE = HashTree.OperationMode.valueOf(opMode);
        fileToSign = new File("src/test/resources/test.log");
        signatureFile = new File("src/test/resources/test.sig");
        filesToDelete = Stream.of(fileToSign, signatureFile).collect(Collectors.toList());
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
        createRandomFile(fileToSign.getPath(), 1, 100);

        SignVerify sv = new SignVerify(false, hashAlgorithm);

        sv.sign(fileToSign, signatureFile);

        appendToFile(fileToSign.getPath(), eventsAsLines(createEvents(10, "newEvent")));

        boolean verif = sv.verify(signatureFile, fileToSign);
        assertFalse(verif);
    }

    @Test
    public void testSignAndVerifyRandomFileAppendAllowed() throws Exception {
        createRandomFile(fileToSign.getPath(), 1, 500);

        SignVerify sv = new SignVerify(true, hashAlgorithm);

        sv.sign(fileToSign, signatureFile);

        appendToFile(fileToSign.getPath(), eventsAsLines(createEvents(127, "newEventSet1.")));
        assertTrue(sv.verify(signatureFile, fileToSign));

        appendToFile(fileToSign.getPath(), eventsAsLines(createEvents(127, "newEventSet2.")));
        assertTrue(sv.verify(signatureFile, fileToSign));
    }

    @Test
    public void testSignAndVerifyRandomFileAlterDataDeleteLine() throws Exception {
        createRandomFile(fileToSign.getPath(), 1, 500);

        SignVerify sv = new SignVerify(false, hashAlgorithm);

        sv.sign(fileToSign, signatureFile);

        alterFileDeleteRandomLine(fileToSign.getPath());
        assertFalse(sv.verify(signatureFile, fileToSign));
    }

    @Test
    public void testSignAndVerifyRandomFileAlterDataChangeLine() throws Exception {
        createRandomFile(fileToSign.getPath(), 1, 500);

        SignVerify sv = new SignVerify(false, hashAlgorithm);

        sv.sign(fileToSign, signatureFile);

        alterFileChangeRandomLine(fileToSign.getPath());
        assertFalse(sv.verify(signatureFile, fileToSign));
    }


}
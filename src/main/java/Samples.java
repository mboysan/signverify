import hashing.IHash;
import ops.FileHasher;
import ops.LogSigner;
import ops.Signature;

import java.io.File;

public class Samples {

    public static void main(String[] args) throws Exception {
        File fileToCheck = new File("src/main/resources/testlog.txt");
        Signature signature = LogSigner.sign(fileToCheck);
        IHash hashToCheck = new FileHasher(fileToCheck).getFileHash();
        boolean isSuccess = LogSigner.verify(signature, hashToCheck);
        System.out.println("File verification " + (isSuccess ? "succeeded!" : "failed!"));
    }
}

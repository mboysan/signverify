import ops.FileHasher;
import ops.SignVerify;
import ops.Signature;

import java.io.File;

public class Samples {

    public static void main(String[] args) throws Exception {
        File fileToCheck = new File("src/main/resources/testlog.txt");
        Signature signature = SignVerify.sign(fileToCheck);
        boolean isSuccess = SignVerify.verify(signature, new FileHasher(fileToCheck));
        System.out.println("File verification " + (isSuccess ? "succeeded!" : "failed!"));
    }
}

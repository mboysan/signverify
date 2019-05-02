package ops;

import hashing.IHash;

import java.io.File;

public class LogSigner {

    public static Signature sign(File file) throws Exception {
        return new Signature(new FileHasher(file.getPath()).getFileHash());
    }

    public static boolean verify(Signature signature, IHash fileHash) {
        return signature.getEncryptedHash().equals(fileHash);
    }

}

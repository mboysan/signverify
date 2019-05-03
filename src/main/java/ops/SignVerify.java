package ops;

import java.io.*;

public class SignVerify {

    public static Signature sign(File file) throws Exception {
        return sign(file, true);
    }

    public static Signature sign(File file, boolean allowAppend) throws Exception {
        return new Signature(new FileHasher(file.getPath()).getFileHash(), allowAppend);
    }

    public static Signature signToFile(File fileToSign, File signatureFile) throws Exception {
        return signToFile(fileToSign, signatureFile, true);
    }

    public static Signature signToFile(File fileToSign, File signatureFile, boolean allowAppend) throws Exception {
        Signature signature = sign(fileToSign, allowAppend);
        try(FileOutputStream f = new FileOutputStream(signatureFile);
            ObjectOutputStream o = new ObjectOutputStream(f)) {
            o.writeObject(signature);
        }
        return signature;
    }

    public static boolean verify(Signature signature, File fileToVerify) throws Exception {
        FileHasher hasher = new FileHasher(fileToVerify);
        if (signature.isAppendAllowed()) {
            return hasher.getFileHashTree().isValidEvent(signature.getEncryptedHash());
        }
        return signature.getEncryptedHash().equals(hasher.getFileHash());
    }

    public static boolean verify(File signatureFile, File fileToVerify) throws Exception {
        try(FileInputStream fi = new FileInputStream(signatureFile);
            ObjectInputStream oi = new ObjectInputStream(fi)) {
            Signature signature = (Signature) oi.readObject();
            return verify(signature, fileToVerify);
        }
    }

}

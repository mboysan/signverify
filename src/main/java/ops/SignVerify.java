package ops;

import hashing.IHash;

import java.io.*;

public class SignVerify {

    private final boolean allowAppend;
    private final String hashAlgorithm;

    public SignVerify(boolean allowAppend) {
        this(allowAppend, "SHA-256");
    }

    public SignVerify(boolean allowAppend, String hashAlgorithm) {
        this.allowAppend = allowAppend;
        this.hashAlgorithm = hashAlgorithm;
    }

    public Signature sign(File fileToSign) throws Exception {
        FileHasher fileHasher = new FileHasher(fileToSign);
        IHash fileHash = fileHasher.getFileHashTree().getRoot().getHash();
        int eventCount = fileHasher.getFileHashTree().getLeafCount();
        return new Signature(fileHash, eventCount, allowAppend);
    }

    public Signature sign(File fileToSign, File signatureFile) throws Exception {
        Signature signature = sign(fileToSign);
        try(FileOutputStream f = new FileOutputStream(signatureFile);
            ObjectOutputStream o = new ObjectOutputStream(f)) {
            o.writeObject(signature);
        }
        return signature;
    }

    public boolean verify(Signature signature, File fileToVerify) throws Exception {
        FileHasher hasher = new FileHasher(fileToVerify, signature.getEventCount());
        if (signature.isAppendAllowed()) {
            return hasher.getFileHashTree().isValidEvent(signature.getEncryptedHash());
        }
        return signature.getEncryptedHash().equals(hasher.getFileHash());
    }

    public boolean verify(File signatureFile, File fileToVerify) throws Exception {
        try(FileInputStream fi = new FileInputStream(signatureFile);
            ObjectInputStream oi = new ObjectInputStream(fi)) {
            Signature signature = (Signature) oi.readObject();
            return verify(signature, fileToVerify);
        }
    }

}

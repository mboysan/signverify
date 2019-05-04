package ops;

import hashing.HashUtils;
import hashing.IHash;

import java.io.*;
import java.util.List;

public class SignVerify {

    private final boolean allowAppend;
    private final String hashAlgorithm;

    public SignVerify(boolean allowAppend) {
        this(allowAppend, HashUtils.getDefaultHashAlgorithm());
    }

    public SignVerify(boolean allowAppend, String hashAlgorithm) {
        this.allowAppend = allowAppend;
        this.hashAlgorithm = hashAlgorithm;
    }

    public Signature sign(File fileToSign) throws Exception {
        FileHasher fileHasher = new FileHasher(fileToSign, hashAlgorithm);
        IHash fileHash = fileHasher.getFileHashTree().getRoot().getHash();
        int eventCount = fileHasher.getFileHashTree().getLeafCount();
        return new Signature(fileHash, eventCount, allowAppend, hashAlgorithm);
    }

    public Signature sign(File fileToSign, File signatureFile) throws Exception {
        Signature signature = sign(fileToSign);
        try(FileOutputStream f = new FileOutputStream(signatureFile);
            ObjectOutputStream o = new ObjectOutputStream(f)) {
            o.writeObject(signature);
            o.flush();
        }
        return signature;
    }

    public boolean verify(Signature signature, File fileToVerify) throws Exception {
        FileHasher hasher = new FileHasher(fileToVerify, signature.getEventCount(), signature.getHashAlgorithm());
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

    public List<IHash> hashChainForEvent(File file, String event) throws Exception {
        return new FileHasher(file, hashAlgorithm).getFileHashTree().extractHashChain(event);
    }

    public String visualizeHashMap(File file) throws Exception {
        return new FileHasher(file, hashAlgorithm).getFileHashTree().visualize();
    }

}

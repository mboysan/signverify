package ee.mboysan.signverify.ops;

import ee.mboysan.signverify.hashing.HashUtils;
import ee.mboysan.signverify.hashing.IHash;
import ee.mboysan.signverify.tree.HashTree;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public final class SignVerify {

    public Signature sign(File fileToSign) throws Exception {
        return sign(fileToSign, false, HashUtils.getDefaultHashAlgorithm());
    }

    public Signature sign(File fileToSign, boolean allowAppend, String hashAlgorithm) throws Exception {
        FileHasher fileHasher = new FileHasher(fileToSign, hashAlgorithm);
        IHash fileHash = fileHasher.getFileHashTree().getRoot().getHash();
        int eventCount = fileHasher.getFileHashTree().getLeafCount();
        return new Signature(fileHash, eventCount, allowAppend, hashAlgorithm);
    }

    public Signature sign(File fileToSign, File signatureFile) throws Exception {
        return sign(fileToSign, signatureFile, false, HashUtils.getDefaultHashAlgorithm());
    }

    public Signature sign(File fileToSign, File signatureFile, boolean allowAppend, String hashAlgorithm) throws Exception {
        Signature signature = sign(fileToSign, allowAppend, hashAlgorithm);
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
            return hasher.getFileHashTree().isValidEvent(signature.getFileHash());
        }
        return signature.getFileHash().equals(hasher.getFileHash());
    }

    public boolean verify(File signatureFile, File fileToVerify) throws Exception {
        try(FileInputStream fi = new FileInputStream(signatureFile);
            ObjectInputStream oi = new ObjectInputStream(fi)) {
            Signature signature = (Signature) oi.readObject();
            return verify(signature, fileToVerify);
        }
    }

    public List<IHash> hashChainForEvent(File file, String event) throws Exception {
        return hashChainForEvent(file, event, HashUtils.getDefaultHashAlgorithm());
    }

    public List<IHash> hashChainForEvent(File file, String event, String hashAlgorithm) throws Exception {
        return hashChainForEvent(file, null, event, hashAlgorithm);
    }

    public List<IHash> hashChainForEvent(File file, File outFile, String event, String hashAlgorithm) throws Exception {
        List<IHash> hashes = new FileHasher(file, hashAlgorithm).getFileHashTree().extractHashChain(event);
        if (outFile != null) {
            try(FileOutputStream f = new FileOutputStream(outFile);
                ObjectOutputStream o = new ObjectOutputStream(f)) {
                for (IHash hash : hashes) {
                    o.writeObject(hash);
                    o.flush();
                }
            }
        }
        return hashes;
    }

    public List<IHash> readHashChainFromFile(File file) throws IOException, ClassNotFoundException {
        List<IHash> hashes = new ArrayList<>();
        try(FileInputStream fi = new FileInputStream(file);
            ObjectInputStream oi = new ObjectInputStream(fi)) {
            for(;;){
                hashes.add((IHash) oi.readObject());
            }
        } catch (EOFException ignore) {}
        return hashes;
    }

    public String visualizeHashMap(File file) throws Exception {
        return visualizeHashMap(file, HashUtils.getDefaultHashAlgorithm());
    }

    public String visualizeHashMap(File file, String hashAlgorithm) throws Exception {
        return visualizeHashMap(file, null, hashAlgorithm);
    }

    public String visualizeHashMap(File file, File outFile, String hashAlgorithm) throws Exception {
        return visualizeHashMap(file, outFile, hashAlgorithm, -1);
    }

    public String visualizeHashMap(File file, File outFile, String hashAlgorithm, int hashLength) throws Exception {
        HashTree hashTree = new FileHasher(file, hashAlgorithm).getFileHashTree();
        String visual = hashLength > 0
                ? hashTree.visualize(hashLength)
                : hashTree.visualize();
        if (outFile != null) {
            Files.write(outFile.toPath(), visual.getBytes());
        }
        return visual;
    }
}

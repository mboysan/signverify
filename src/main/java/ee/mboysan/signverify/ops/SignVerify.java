package ee.mboysan.signverify.ops;

import ee.mboysan.signverify.hashing.HashUtils;
import ee.mboysan.signverify.hashing.IHash;
import ee.mboysan.signverify.tree.HashTree;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The API for signing and verifying a file.
 */
public final class SignVerify {

    /**
     * Signs a given file with the {@link HashUtils#getDefaultHashAlgorithm()} producing a signature.
     * After signing, any modifications (including appends) will result the integrity verification fail.
     *
     * @param fileToSign file to sign.
     * @return a new signature containing the info about the file.
     * @throws Exception if signing fails.
     */
    public Signature sign(File fileToSign) throws Exception {
        return sign(fileToSign, false, HashUtils.getDefaultHashAlgorithm());
    }

    /**
     * Signs a given file with the provided hashAlgorithm producing a signature.
     * If allowAppend flag is set, the file will be treated as append-only and further verifications will succeed
     * if the file content is not changed or only new lines are added to the file. Otherwise, the file will be
     * treated as static and any modifications will result in further validation failures.
     *
     * @param fileToSign file to sign.
     * @param allowAppend true, if append-only log file, false otherwise.
     * @param hashAlgorithm the hash algorithm to use when producing the hash tree.
     * @return a new signature containing the info about the file.
     * @throws Exception if signing fails.
     */
    public Signature sign(File fileToSign, boolean allowAppend, String hashAlgorithm) throws Exception {
        FileHasher fileHasher = new FileHasher(fileToSign, hashAlgorithm);
        IHash fileHash = fileHasher.getFileHashTree().getRoot().getHash();
        int eventCount = fileHasher.getFileHashTree().getLeafCount();
        return new Signature(fileHash, eventCount, allowAppend, hashAlgorithm);
    }

    /**
     * After calling {@link #sign(File)} method, the signature will be persisted to the provided signatureFile.
     *
     * @param fileToSign file to sign.
     * @param signatureFile output signature file.
     * @return a new signature containing the info about the file.
     * @throws Exception if signing fails.
     */
    public Signature sign(File fileToSign, File signatureFile) throws Exception {
        return sign(fileToSign, signatureFile, false, HashUtils.getDefaultHashAlgorithm());
    }

    /**
     * After calling {@link #sign(File, boolean, String)}, the signature will be persisted to the provided
     * signatureFile.
     *
     * @param fileToSign file to sign.
     * @param signatureFile output signature file.
     * @param allowAppend true, if append-only log file, false otherwise.
     * @param hashAlgorithm the hash algorithm to use when producing the hash tree.
     * @return a new signature containing the info about the file.
     * @throws Exception if signing fails.
     */
    public Signature sign(File fileToSign, File signatureFile, boolean allowAppend, String hashAlgorithm) throws Exception {
        Signature signature = sign(fileToSign, allowAppend, hashAlgorithm);
        try(FileOutputStream f = new FileOutputStream(signatureFile);
            ObjectOutputStream o = new ObjectOutputStream(f)) {
            o.writeObject(signature);
            o.flush();
        }
        return signature;
    }

    /**
     * Checks the integrity of a file with the provided signature. If the file integrity has changed, the verification
     * fails.
     *
     * @param signature signature to verify against fileToVerify.
     * @param fileToVerify log file to verify.
     * @return true if verification succeeds, false otherwise.
     * @throws Exception if any problem occurs while checking the integrity of the file.
     */
    public boolean verify(Signature signature, File fileToVerify) throws Exception {
        FileHasher hasher = new FileHasher(fileToVerify, signature.getEventCount(), signature.getHashAlgorithm());
        if (signature.isAppendAllowed()) {
            return hasher.getFileHashTree().isValidEvent(signature.getFileHash());
        }
        return signature.getFileHash().equals(hasher.getFileHash());
    }

    /**
     * After obtaining the signature object from the signatureFile, calls {@link #verify(Signature, File)} to verify
     * fileToVerify with the signature.
     *
     * @param signatureFile  file containing the signature pojo.
     * @param fileToVerify log file to verify.
     * @return true if verification succeeds, false otherwise.
     * @throws Exception if any problem occurs while checking the integrity of the file.
     */
    public boolean verify(File signatureFile, File fileToVerify) throws Exception {
        try(FileInputStream fi = new FileInputStream(signatureFile);
            ObjectInputStream oi = new ObjectInputStream(fi)) {
            Signature signature = (Signature) oi.readObject();
            return verify(signature, fileToVerify);
        }
    }

    /**
     * Returns a hash chain represented as a list for the given event. The first element of the list contains the
     * given event's hash (leaf hash) and the last element of the list contains the complete hash representing the whole log file
     * (root hash). </br>
     * When extracting the hash chain, {@link HashUtils#getDefaultHashAlgorithm()} is used for hash function.
     *
     * @param file log file to check.
     * @param event event/input to check/extract the hash chain for.
     * @return list containing the hash chain to calculate the root hash. In format
     *         [leafHash, [concat1, concat2, ...], rootHash)
     * @throws Exception if event not found or hash chain extraction fails.
     */
    public List<IHash> hashChainForEvent(File file, String event) throws Exception {
        return hashChainForEvent(file, event, HashUtils.getDefaultHashAlgorithm());
    }

    /**
     * Returns a hash chain as a list calculated with the hashAlgorithm provided for the given event. The first element
     * of the list contains the given event's hash (leaf hash) and the last element of the list contains the complete
     * hash representing the whole log file (root hash).
     *
     * @param file          log file to check.
     * @param event         event/input to check/extract the hash chain for.
     * @param hashAlgorithm Hash algorithm used for the hash function.
     * @return list containing the hash chain to calculate the root hash. In format
     *         [leafHash, [concat1, concat2, ...], rootHash)
     * @throws Exception if event not found or hash chain extraction fails.
     */
    public List<IHash> hashChainForEvent(File file, String event, String hashAlgorithm) throws Exception {
        return hashChainForEvent(file, null, event, hashAlgorithm);
    }

    /**
     * Returns a hash chain as a list calculated with the hashAlgorithm provided for the given event. The first element
     * of the list contains the given event's hash (leaf hash) and the last element of the list contains the complete
     * hash representing the whole log file (root hash). If outFile is provided, the hash objects are serialized
     * and written to the provided file.
     *
     * @param file          log file to check.
     * @param outFile       output file for the extracted hash chain.
     * @param event         event/input to check/extract the hash chain for.
     * @param hashAlgorithm Hash algorithm used for the hash function.
     * @return list containing the hash chain to calculate the root hash. In format
     *         [leafHash, [concat1, concat2, ...], rootHash)
     * @throws Exception if event not found or hash chain extraction fails.
     */
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

    /**
     * Reads the hash chain from the provided file assuming the file contains serialized {@link IHash} objects.
     *
     * @param file file containing {@link IHash} objects.
     * @return list containing the hash chain to calculate the root hash. In format
     *         [leafHash, [concat1, concat2, ...], rootHash)
     * @throws IOException if file cannot be read.
     * @throws ClassNotFoundException if objects in file is not of type {@link IHash}.
     */
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

    /**
     * Calls {@link #visualizeHashMap(File, String, int)} with {@link HashUtils#getDefaultHashAlgorithm()} and
     * hashLength not modified.
     */
    public String visualizeHashMap(File file) throws Exception {
        return visualizeHashMap(file, HashUtils.getDefaultHashAlgorithm());
    }

    /**
     * Calls {@link #visualizeHashMap(File, String, int)} with the provided hashAlgorithm without modifying
     * the hashLength.
     */
    public String visualizeHashMap(File file, String hashAlgorithm) throws Exception {
        return visualizeHashMap(file, null, -1);
    }

    /**
     * Visualizes the hash tree representing the given file, calculated with the hashAlgorithm. If hashLength is
     * greater than -1, each hash string represented in hex is shortened to this length.
     *
     * @param file file to visualize as a hash tree.
     * @param hashAlgorithm hash algorithm to use for the hash tree.
     * @param hashLength    the string length of each hash node. If less than 0, full length of the hash string in hex
     *                      is used.
     * @return a string representing the complete hash tree.
     * @throws Exception if visualization fails.
     */
    public String visualizeHashMap(File file, String hashAlgorithm, int hashLength) throws Exception {
        HashTree hashTree = new FileHasher(file, hashAlgorithm).getFileHashTree();
        String visual = hashLength > 0
                ? hashTree.visualize(hashLength)
                : hashTree.visualize();
        return visual;
    }
}

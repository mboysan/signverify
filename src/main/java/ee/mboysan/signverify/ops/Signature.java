package ee.mboysan.signverify.ops;

import ee.mboysan.signverify.hashing.IHash;

import java.io.Serializable;

/**
 * A pojo for signature objects.
 */
public class Signature implements Serializable {

    /**
     * hash of the file.
     */
    private final IHash fileHash;
    /**
     * For append-only files. True if appending to a file is allowed. False for static/unmodifiable files.
     */
    private final boolean allowAppend;
    /**
     * File line count.
     */
    private final int eventCount;
    /**
     * Hash algorithm used when creating the file hash.
     */
    private final String hashAlgorithm;

    /**
     * @param fileHash see {@link #fileHash}
     * @param eventCount see {@link #eventCount}
     * @param allowAppend see {@link #allowAppend}
     * @param hashAlgorithm see {@link #hashAlgorithm}
     */
    Signature(IHash fileHash, int eventCount, boolean allowAppend, String hashAlgorithm) {
        this.fileHash = fileHash;
        this.eventCount = eventCount;
        this.allowAppend = allowAppend;
        this.hashAlgorithm = hashAlgorithm;
    }

    /**
     * @return see {@link #fileHash}
     */
    IHash getFileHash() {
        return fileHash;
    }

    /**
     * @return see {@link #eventCount}
     */
    int getEventCount() {
        return eventCount;
    }

    /**
     * @return see {@link #allowAppend}
     */
    boolean isAppendAllowed() {
        return allowAppend;
    }

    /**
     * @return see {@link #hashAlgorithm}
     */
    String getHashAlgorithm() {
        return hashAlgorithm;
    }

    @Override
    public String toString() {
        return "Signature{" +
                "fileHash=" + fileHash +
                ", allowAppend=" + allowAppend +
                ", eventCount=" + eventCount +
                ", hashAlgorithm='" + hashAlgorithm + '\'' +
                '}';
    }
}

package ee.mboysan.signverify.ops;

import ee.mboysan.signverify.hashing.IHash;

import java.io.Serializable;

public class Signature implements Serializable {

    private final IHash fileHash;
    private final boolean allowAppend;
    private final int eventCount;
    private final String hashAlgorithm;

    Signature(IHash fileHash, int eventCount, boolean allowAppend, String hashAlgorithm) {
        this.fileHash = fileHash;
        this.eventCount = eventCount;
        this.allowAppend = allowAppend;
        this.hashAlgorithm = hashAlgorithm;
    }

    IHash getFileHash() {
        return fileHash;
    }

    int getEventCount() {
        return eventCount;
    }

    boolean isAppendAllowed() {
        return allowAppend;
    }

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

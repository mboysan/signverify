package ops;

import hashing.IHash;

import java.io.Serializable;

public class Signature implements Serializable {

    private final IHash encryptedHash;
    private final boolean allowAppend;
    private final int eventCount;
    private final String hashAlgorithm;

    Signature(IHash encryptedHash, int eventCount, boolean allowAppend, String hashAlgorithm) {
        this.encryptedHash = encryptedHash;
        this.eventCount = eventCount;
        this.allowAppend = allowAppend;
        this.hashAlgorithm = hashAlgorithm;
    }

    IHash getEncryptedHash() {
        return encryptedHash;
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
                "encryptedHash=" + encryptedHash +
                ", allowAppend=" + allowAppend +
                ", eventCount=" + eventCount +
                ", hashAlgorithm='" + hashAlgorithm + '\'' +
                '}';
    }
}

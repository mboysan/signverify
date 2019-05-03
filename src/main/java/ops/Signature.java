package ops;

import hashing.IHash;

import java.io.Serializable;

public class Signature implements Serializable {

    private final IHash encryptedHash;
    private final boolean allowAppend;
    private final int eventCount;

    Signature(IHash encryptedHash, int eventCount, boolean allowAppend) {
        this.encryptedHash = encryptedHash;
        this.eventCount = eventCount;
        this.allowAppend = allowAppend;
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

    @Override
    public String toString() {
        return "Signature{" +
                "encryptedHash=" + encryptedHash +
                ", allowAppend=" + allowAppend +
                ", eventCount=" + eventCount +
                '}';
    }
}

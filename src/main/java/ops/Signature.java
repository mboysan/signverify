package ops;

import hashing.IHash;

import java.io.Serializable;

public class Signature implements Serializable {

    private final IHash encryptedHash;
    private final boolean allowAppend;

    Signature(IHash encryptedHash, boolean allowAppend) {
        this.encryptedHash = encryptedHash;
        this.allowAppend = allowAppend;
    }

    IHash getEncryptedHash() {
        return encryptedHash;
    }

    boolean isAppendAllowed() {
        return allowAppend;
    }

    @Override
    public String toString() {
        return "Signature{" +
                "encryptedHash=" + encryptedHash +
                ", allowAppend=" + allowAppend +
                '}';
    }
}

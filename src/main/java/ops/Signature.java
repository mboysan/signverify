package ops;

import hashing.IHash;

public class Signature {

    private final IHash encryptedHash;

    Signature(IHash encryptedHash) {
        this.encryptedHash = encryptedHash;
    }

    IHash getEncryptedHash() {
        return encryptedHash;
    }
}

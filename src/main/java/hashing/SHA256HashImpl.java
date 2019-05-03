package hashing;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class SHA256HashImpl implements IHash {

    private final byte[] hash;
    private Position position;

    public SHA256HashImpl(String event) throws NoSuchAlgorithmException {
        this.hash = generateHash(event);
    }

    private SHA256HashImpl(byte[] hash) throws NoSuchAlgorithmException {
        this.hash = generateHash(hash);
    }

    @Override
    public byte[] generateHash(String event) throws NoSuchAlgorithmException {
        return generateHash(event.getBytes(StandardCharsets.UTF_8));
    }

    private byte[] generateHash(byte[] mergedHash) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance("SHA-256").digest(mergedHash);
    }

    @Override
    public SHA256HashImpl mergeAndCreateNewHash(IHash hashToMerge) throws NoSuchAlgorithmException {
        if (!(hashToMerge instanceof SHA256HashImpl)) {
            throw new IllegalArgumentException("Merge failed: implementation classes do not match: " + hashToMerge.getClass());
        }
        SHA256HashImpl toMerge = (SHA256HashImpl) hashToMerge;
        byte[] mergedHash = new byte[this.hash.length + toMerge.hash.length];
        System.arraycopy(this.hash, 0, mergedHash, 0, this.hash.length);
        System.arraycopy(toMerge.hash, 0, mergedHash, this.hash.length, toMerge.hash.length);
        SHA256HashImpl newHash = new SHA256HashImpl(mergedHash);
        setPosition(Position.LEFT);
        hashToMerge.setPosition(Position.RIGHT);
        return newHash;
    }

    @Override
    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public Position getPosition() {
        return this.position;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SHA256HashImpl)) {
            return false;
        }
        SHA256HashImpl other = (SHA256HashImpl) obj;
        return Arrays.equals(this.hash, other.hash);
    }

    /**
     * Taken from: https://www.baeldung.com/sha-256-hashing-java
     * @param hash
     * @return
     */
    private static String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    @Override
    public String toString() {
        return bytesToHex(hash);
    }
}

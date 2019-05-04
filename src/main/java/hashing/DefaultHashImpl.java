package hashing;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class DefaultHashImpl implements IHash {

    private final byte[] hash;
    private Position position;
    private final String algorithm;

    DefaultHashImpl(String event, String algorithm) throws NoSuchAlgorithmException {
        this.algorithm = algorithm;
        this.hash = generateHash(event);
    }

    private DefaultHashImpl(byte[] hash, String algorithm) throws NoSuchAlgorithmException {
        this.algorithm = algorithm;
        this.hash = generateHash(hash);
    }

    @Override
    public byte[] generateHash(String event) throws NoSuchAlgorithmException {
        return generateHash(event.getBytes(StandardCharsets.UTF_8));
    }

    private byte[] generateHash(byte[] mergedHash) throws NoSuchAlgorithmException {
        return MessageDigest.getInstance(algorithm).digest(mergedHash);
    }

    @Override
    public DefaultHashImpl mergeAndCreateNewHash(IHash hashToMerge) throws NoSuchAlgorithmException {
        if (!(hashToMerge instanceof DefaultHashImpl)) {
            throw new IllegalArgumentException("Merge failed: implementation classes do not match: " + hashToMerge.getClass());
        }
        DefaultHashImpl toMerge = (DefaultHashImpl) hashToMerge;
        byte[] mergedHash = new byte[this.hash.length + toMerge.hash.length];
        System.arraycopy(this.hash, 0, mergedHash, 0, this.hash.length);
        System.arraycopy(toMerge.hash, 0, mergedHash, this.hash.length, toMerge.hash.length);
        DefaultHashImpl newHash = new DefaultHashImpl(mergedHash, algorithm);
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
        if (!(obj instanceof DefaultHashImpl)) {
            return false;
        }
        DefaultHashImpl other = (DefaultHashImpl) obj;
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

package ee.mboysan.signverify.hashing;

/**
 * A utility class for general hashing operations.
 */
public class HashUtils {

    /**
     * Default hash algorithm to use for hashing functions.
     */
    private static String defaultHashAlgorithm = "SHA-256";

    /**
     * @return {@link #defaultHashAlgorithm}.
     */
    public static String getDefaultHashAlgorithm() {
        return defaultHashAlgorithm;
    }

    /**
     * Creates a hash object with the given event and the {@link #defaultHashAlgorithm}.
     * @param event event/input to create hash for.
     * @return Hash object created.
     * @throws Exception if hash creation fails.
     */
    public static IHash createHash(String event) throws Exception {
        return createHash(event, defaultHashAlgorithm);
    }

    /**
     * Creates a hash object with the given event and the hashAlgorithm provided.
     * @param event         event/input to create hash for.
     * @param hashAlgorithm hash algorithm used when creating the hash.
     * @return Hash object created.
     * @throws Exception if hash creation fails.
     */
    public static IHash createHash(String event, String hashAlgorithm) throws Exception {
        switch (hashAlgorithm) {
            case "SHA-256":
            case "SHA-1":
            case "MD5":
                return new DefaultHashImpl(event, hashAlgorithm);
            default:
                throw new IllegalStateException("Hash algorithm not recognized: " + hashAlgorithm);
        }
    }

    /**
     * Merges hash1 (on left) with hash2 (on right) producing a new hash object: (hash1 | hash2).
     * @param hash1 hash on the left.
     * @param hash2 hash on the right.
     * @return a new Hash object created merging (hash1 | hash2).
     * @throws Exception if hash merge fails.
     */
    public static IHash mergeHashes(IHash hash1, IHash hash2) throws Exception {
        return hash1.mergeAndCreateNewHash(hash2);
    }

}

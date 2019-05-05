package ee.mboysan.signverify.hashing;

public class HashUtils {

    static String defaultHashAlgorithm = "SHA-256";

    public static String getDefaultHashAlgorithm() {
        return defaultHashAlgorithm;
    }

    public static IHash createHash(String event) throws Exception {
        return createHash(event, defaultHashAlgorithm);
    }

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

    public static IHash mergeHashes(IHash hash1, IHash hash2) throws Exception {
        return hash1.mergeAndCreateNewHash(hash2);
    }

}

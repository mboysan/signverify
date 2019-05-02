package hashing;

public class HashUtils {

    public static Class<? extends IHash> defaultHasherImplClass = SHA256HashImpl.class;

    public static IHash createHash(String event) throws Exception {
        return createHash(event, defaultHasherImplClass);
    }

    public static IHash createHash(String event, Class<? extends IHash> hasherImplClass) throws Exception {
        return hasherImplClass.getConstructor(String.class).newInstance(event);
    }

    public static IHash mergeHashes(IHash hash1, IHash hash2) throws Exception {
        return hash1.mergeAndCreateNewHash(hash2);
    }

}

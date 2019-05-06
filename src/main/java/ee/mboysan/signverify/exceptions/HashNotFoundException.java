package ee.mboysan.signverify.exceptions;

/**
 * If the hash could not be found in the built {@link ee.mboysan.signverify.tree.HashTree}.
 */
public class HashNotFoundException extends Exception {
    public HashNotFoundException(String message) {
        super(message);
    }
}

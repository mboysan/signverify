package ee.mboysan.signverify.exceptions;

/**
 * Intended to be used for problems related to {@link ee.mboysan.signverify.ops.FileHasher}.
 */
public class FileHashingFailedException extends Exception {
    public FileHashingFailedException(String message) {
        super(message);
    }

    public FileHashingFailedException(Throwable cause) {
        super(cause);
    }
}

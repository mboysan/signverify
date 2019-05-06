package ee.mboysan.signverify.exceptions;

/**
 * Created in case the {@link ee.mboysan.signverify.tree.HashTree} building is failed.
 */
public class TreeConstructionFailedException extends Exception {
    public TreeConstructionFailedException(String message) {
        super(message);
    }
}

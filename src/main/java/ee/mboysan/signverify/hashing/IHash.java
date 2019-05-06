package ee.mboysan.signverify.hashing;

import java.io.Serializable;

public interface IHash extends Serializable {
    /**
     * @param event input/event string to generate a hash for.
     * @return a hash object generated. Usually byte[].
     * @throws Exception in case the hash generation fails.
     */
    Object generateHash(String event) throws Exception;

    /**
     * Merges the current hash with the <code>hashToMerge</code> and creates a new hash object.
     * @param hashToMerge hash to merge with.
     * @return a new hash object merged with the current hash and the given one.
     * @throws Exception in case the hash merge fails.
     */
    IHash mergeAndCreateNewHash(IHash hashToMerge) throws Exception;

    /**
     * Sets the position of the node containing this hash relative to parent node.
     * @param position {@link Position} to set.
     */
    void setPosition(Position position);

    /**
     * Gets the position of the node containing this hash relative to parent node.
     */
    Position getPosition();

    /**
     * Indicates the node's position containing this hash.
     */
    enum Position {
        LEFT, RIGHT
    }
}

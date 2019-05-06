package ee.mboysan.signverify.tree;

/**
 * Represents a leaf of the tree.
 */
class HashLeaf extends HashNode {

    HashLeaf(String event, String hashAlgorithm) throws Exception {
        super(event, hashAlgorithm);
    }
}

package ee.mboysan.signverify.tree;

import ee.mboysan.signverify.hashing.HashUtils;
import ee.mboysan.signverify.hashing.IHash;

/**
 * Represents a node in the tree.
 */
public class HashNode {

    /** Node's parent. */
    private HashNode parentNode;

    /** node on the left. */
    private HashNode leftNode;

    /** node on the right. */
    private HashNode rightNode;

    /**
     * this node's level, i.e. number of nodes between this node and it's farthest leaf in this branch.
     */
    private int depth = 0;

    /**
     * The node's hash.
     */
    private final IHash hash;

    /**
     * Constructs a node representing the hash of the given event.
     * @param event event represented by this node.
     * @param hashAlgorithm hash algorithm used for creating the hash for the event.
     * @throws Exception if hashing fails.
     */
    HashNode(String event, String hashAlgorithm) throws Exception {
        this.hash = HashUtils.createHash(event, hashAlgorithm);
    }

    /**
     * Constructs a parent node from the given nodes. This has a side effect of modifying the provided nodes' parent
     * to this node. The parent node's hash is calculated by concatenating the hashes like
     * (lefNode.hash | rightNode.hash).
     * @param leftNode  node on the left.
     * @param rightNode node on the right.
     * @throws Exception if hashing fails.
     */
    HashNode(HashNode leftNode, HashNode rightNode) throws Exception {
        this.leftNode = leftNode;
        this.rightNode = rightNode;
        leftNode.parentNode = this;
        rightNode.parentNode = this;
        this.depth = Math.max(leftNode.getDepth(), rightNode.getDepth()) + 1;

        this.hash = HashUtils.mergeHashes(leftNode.hash, rightNode.hash);

    }

    /**
     * @return see {@link #parentNode}.
     */
    HashNode getParentNode() {
        return parentNode;
    }

    /**
     * @return see {@link #leftNode}.
     */
    HashNode getLeftNode() {
        return leftNode;
    }

    /**
     * @return see {@link #rightNode}.
     */
    HashNode getRightNode() {
        return rightNode;
    }

    /**
     * @return see {@link #depth}.
     */
    int getDepth() {
        return depth;
    }

    /**
     * @return see {@link #hash}.
     */
    public IHash getHash() {
        return hash;
    }
}

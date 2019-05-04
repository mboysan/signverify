package tree;

import hashing.HashUtils;
import hashing.IHash;

public class HashNode {

    private HashNode parentNode;
    private HashNode leftNode;
    private HashNode rightNode;

    private int depth = 0;

    private final IHash hash;

    HashNode(String event, String hashAlgorithm) throws Exception {
        this.hash = HashUtils.createHash(event, hashAlgorithm);
    }

    HashNode(HashNode leftNode, HashNode rightNode) throws Exception {
        this.leftNode = leftNode;
        this.rightNode = rightNode;
        leftNode.parentNode = this;
        rightNode.parentNode = this;
        this.depth = Math.max(leftNode.getDepth(), rightNode.getDepth()) + 1;

        this.hash = HashUtils.mergeHashes(leftNode.hash, rightNode.hash);

    }

    HashNode getParentNode() {
        return parentNode;
    }

    HashNode getLeftNode() {
        return leftNode;
    }

    HashNode getRightNode() {
        return rightNode;
    }

    int getDepth() {
        return depth;
    }

    public IHash getHash() {
        return hash;
    }
}

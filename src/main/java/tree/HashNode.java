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
        leftNode.setParentNode(this);
        rightNode.setParentNode(this);
        this.depth = Math.max(leftNode.getDepth(), rightNode.getDepth()) + 1;

        this.hash = HashUtils.mergeHashes(leftNode.hash, rightNode.hash);

    }

    public boolean isLeftNode() {
        return parentNode != null && this.equals(parentNode.leftNode);
    }

    public boolean isRightNode() {
        return parentNode != null && this.equals(parentNode.rightNode);
    }

    public HashNode getParentNode() {
        return parentNode;
    }

    private void setParentNode(HashNode parentNode) {
        this.parentNode = parentNode;
    }

    public HashNode getLeftNode() {
        return leftNode;
    }

    public HashNode getRightNode() {
        return rightNode;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public IHash getHash() {
        return hash;
    }

    public boolean isSameHash(IHash hash) {
        return this.hash.equals(hash);
    }
}

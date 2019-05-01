package tree;

public class HashNode {

    private HashNode parentNode;
    private HashNode leftNode;
    private HashNode rightNode;

    private int depth = 0;

    protected byte[] hash;

    private int coverageStart;
    private int coverageEnd;

    HashNode(String event) {
    }

    HashNode(HashNode leftNode, HashNode rightNode) {
        this.leftNode = leftNode;
        this.rightNode = rightNode;
        leftNode.setParentNode(this);
        rightNode.setParentNode(this);
        this.depth = Math.max(leftNode.getDepth(), rightNode.getDepth()) + 1;
    }

    public boolean isLeafParent() {
        return leftNode instanceof HashLeaf && rightNode instanceof HashLeaf;
    }

    public boolean isLeaf() {
        return leftNode == null && rightNode == null;
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

    public void setLeftNode(HashNode leftNode) {
        this.leftNode = leftNode;
    }

    public HashNode getRightNode() {
        return rightNode;
    }

    public void setRightNode(HashNode rightNode) {
        this.rightNode = rightNode;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }
}

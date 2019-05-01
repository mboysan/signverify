package tree;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Merkle Tree implementation.
 */
public class HashTree {

    private Deque<HashNode> nodeStack = new ArrayDeque<>();
    private HashNode root;

    private HashTree() {

    }

    private void addNode(HashNode node) {
        if (nodeStack.isEmpty()) {
            nodeStack.push(node);
        } else {
            HashNode prevNode = nodeStack.pop();
            if (prevNode.getDepth() == node.getDepth()) {
                HashNode parent = new HashNode(prevNode, node);
                addNode(parent);
            } else {
                nodeStack.push(prevNode);   // put it back
                nodeStack.push(node);
            }
        }
    }

    private HashTree construct() throws IllegalStateException {
        if (nodeStack.isEmpty()) {
            throw new IllegalStateException("Construction failed: There are no items in the tree.");
        }
        // merge dangling nodes
        while (!nodeStack.isEmpty()) {
            HashNode parentRight = nodeStack.poll();
            HashNode parentLeft = nodeStack.pollFirst();
            if (parentLeft == null) {
                root = parentRight;
                return this;
            }
            HashNode newParent = new HashNode(parentLeft, parentRight);
            nodeStack.push(newParent);
        }
        throw new IllegalStateException("Construction failed: The tree is in invalid state.");
    }

    public HashNode getRoot() {
        return root;
    }

    public static HashTreeBuilder builder() {
        return new HashTreeBuilder();
    }

    public static class HashTreeBuilder {

        private HashTree hashTree;

        private HashTreeBuilder(){
            this.hashTree = new HashTree();
        }

        public HashTreeBuilder appendEvent(String event) {
            hashTree.addNode(new HashLeaf(event));
            return this;
        }

        public HashTree build() {
            return hashTree.construct();
        }

    }
}

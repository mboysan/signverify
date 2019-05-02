package tree;

import exceptions.HashNotFoundException;
import exceptions.TreeConstructionFailedException;
import hashing.HashUtils;
import hashing.IHash;
import hashing.SHA256HashImpl;

import java.util.*;

/**
 * Merkle Tree implementation.
 */
public class HashTree {

    private Deque<HashNode> nodeStack = new ArrayDeque<>();
    private Map<String, HashLeaf> leaves = new HashMap<>();
    private HashNode root;

    private HashTree() {
    }

    private void addNode(HashNode node) throws Exception {
        if (node instanceof HashLeaf) {
            leaves.put(node.getHash().toString(), (HashLeaf) node);
        }
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

    private void merge(HashTree other) throws Exception {
        if (other == null) {
            return;
        }
        addNode(other.getRoot());
        this.leaves.putAll(other.leaves);
    }

    private HashTree construct() throws Exception {
        if (nodeStack.isEmpty()) {
            throw new TreeConstructionFailedException("Construction failed: There are no items in the tree.");
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
        throw new TreeConstructionFailedException("Construction failed: The tree is in invalid state.");
    }

    private HashLeaf findLeaf(String event) throws Exception {
        HashLeaf leaf = leaves.get(HashUtils.createHash(event).toString());
        if (leaf == null) {
            throw new HashNotFoundException("Hash with the event not found: " + event);
        }
        return leaf;
    }

    private void extractHashChain(HashNode node, List<IHash> chain) {
        if (node == null) {
            return;
        }
        HashNode parent = node.getParentNode();
        if (parent == null) {
            return;
        }
        HashNode parentRight = parent.getRightNode();
        HashNode parentLeft = parent.getLeftNode();
        if (parentRight != null && !parentRight.equals(node)) {
            chain.add(parentRight.getHash());
        }
        if (parentLeft != null && !parentLeft.equals(node)) {
            chain.add(parentLeft.getHash());
        }
        extractHashChain(parent, chain);
    }

    public List<IHash> extractHashChain(String eventToCheck) throws HashNotFoundException, Exception {
        HashLeaf leaf = findLeaf(eventToCheck);
        List<IHash> hashes = new ArrayList<>();
        extractHashChain(leaf, hashes);
        return hashes;
    }

    public boolean isValidEvent(String eventToCheck) throws Exception {
        List<IHash> hashChain = extractHashChain(eventToCheck);
        HashLeaf leaf = findLeaf(eventToCheck);
        IHash mergedHash = leaf.getHash();
        for (IHash iHash : hashChain) {
            mergedHash = iHash.getPosition() == IHash.Position.RIGHT
                    ? HashUtils.mergeHashes(mergedHash, iHash)
                    : HashUtils.mergeHashes(iHash, mergedHash);
        }
        return getRoot().getHash().equals(mergedHash);
    }


    public HashNode getRoot() {
        return root;
    }

    private void visualize(HashNode node, int distFromRoot, StringBuilder sb) {
        if (node == null) {
            return;
        }
        if (distFromRoot == 1) {
            sb.append(node.getHash());
        }
        HashNode rightNode = node.getRightNode();
        if (rightNode != null) {
            sb.append("-").append(rightNode.getHash());
        } else {
            sb.append(String.format("%n"));
        }
        visualize(rightNode, distFromRoot + 1, sb);

        HashNode leftNode = node.getLeftNode();
        if (leftNode != null) {
            String h = node.getHash().toString();
            for (int i = 0; i < distFromRoot * h.length(); i++) {
                sb.append(" ");
            }
            sb.append("\\");
            sb.append(leftNode.getHash());
        }
        visualize(leftNode, distFromRoot + 1, sb);
    }

    public String visualize() {
        StringBuilder sb = new StringBuilder();
        visualize(getRoot(), 1, sb);
        return sb.toString();
    }

    public static HashTreeBuilder builder() {
        return new HashTreeBuilder();
    }

    public static class HashTreeBuilder {

        private HashTree hashTree;
        private boolean isBuilt = false;

        private HashTreeBuilder(){
            this.hashTree = new HashTree();
        }

        public HashTreeBuilder appendEvent(String event) throws Exception {
            validateAction();
            hashTree.addNode(new HashLeaf(event));
            return this;
        }

        public HashTreeBuilder mergeTree(HashTree treeToMerge) throws Exception {
            validateAction();
            hashTree.merge(treeToMerge);
            return this;
        }

        public HashTree build() throws Exception {
            validateAction();
            isBuilt = true;
            return hashTree.construct();
        }

        private void validateAction() {
            if (isBuilt) {
                throw new IllegalStateException("Hash tree already built. Cannot operate on it anymore.");
            }
        }

    }
}

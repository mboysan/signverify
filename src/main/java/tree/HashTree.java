package tree;

import exceptions.HashNotFoundException;
import exceptions.TreeConstructionFailedException;
import hashing.HashUtils;
import hashing.IHash;

import java.util.*;

/**
 * Merkle Tree implementation.
 */
public class HashTree {

    private Deque<HashNode> nodeStack = new ArrayDeque<>();
    private Map<String, HashNode> nodeMap = new HashMap<>();
    private HashNode root;
    private int leafCount = 0;

    private HashTree() {
    }

    private void addNode(HashNode node) throws Exception {
        if (node instanceof HashLeaf) {
            leafCount++;
        }
        nodeMap.put(node.getHash().toString(), node);
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
        leafCount += other.leafCount;
        addNode(other.getRoot());
        this.nodeMap.putAll(other.nodeMap);
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

    private HashNode findNode(IHash hash) throws HashNotFoundException {
        HashNode node = nodeMap.get(hash.toString());
        if (node == null) {
            throw new HashNotFoundException("[" + hash + "]");
        }
        return nodeMap.get(hash.toString());
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
        return extractHashChain(HashUtils.createHash(eventToCheck));
    }

    public List<IHash> extractHashChain(IHash eventHash) throws HashNotFoundException {
        HashNode node = findNode(eventHash);
        List<IHash> hashes = new ArrayList<>();
        extractHashChain(node, hashes);
        return hashes;
    }

    public boolean isValidEvent(String eventToCheck) throws Exception {
        return isValidEvent(HashUtils.createHash(eventToCheck));
    }

    public boolean isValidEvent(IHash eventHash) throws Exception {
        List<IHash> hashChain = extractHashChain(eventHash);
        HashNode node = findNode(eventHash);
        IHash mergedHash = node.getHash();
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

    public int getLeafCount() {
        return leafCount;
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

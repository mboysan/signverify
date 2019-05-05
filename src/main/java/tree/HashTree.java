package tree;

import exceptions.HashNotFoundException;
import exceptions.TreeConstructionFailedException;
import hashing.HashUtils;
import hashing.IHash;

import java.util.*;

/**
 * Merkle Tree implementation.
 */
public abstract class HashTree {

    public static OperationMode OPERATION_MODE = OperationMode.MEM;

    private Deque<HashNode> nodeStack = new ArrayDeque<>();
    private HashNode root;
    private int leafCount = 0;

    private final String hashAlgorithm;

    HashTree(String hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }

    void addNode(HashNode node) throws Exception {
        if (node instanceof HashLeaf) {
            leafCount++;
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

    void merge(HashTree other) throws Exception {
        if (other == null) {
            return;
        }
        if (!(other.getRoot() instanceof HashLeaf)) {
            /* if the root of the other tree to merge is a leaf, we do not append its leafCount
               because it will be added in the addNode() method.
             */
            leafCount += other.leafCount;
        }
        addNode(other.getRoot());
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

    abstract HashNode findNode(IHash hash) throws HashNotFoundException;

    private void _extractHashChain(HashNode node, List<IHash> chain) {
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
        _extractHashChain(parent, chain);
    }

    public List<IHash> extractHashChain(String eventToCheck) throws HashNotFoundException, Exception {
        return extractHashChain(HashUtils.createHash(eventToCheck, hashAlgorithm));
    }

    public List<IHash> extractHashChain(IHash eventHash) throws HashNotFoundException {
        HashNode node = findNode(eventHash);
        List<IHash> hashes = new ArrayList<>();
        _extractHashChain(node, hashes);
        return hashes;
    }

    public boolean isValidEvent(String eventToCheck) throws Exception {
        return isValidEvent(HashUtils.createHash(eventToCheck, hashAlgorithm));
    }

    public boolean isValidEvent(IHash eventHash) throws Exception {
        List<IHash> hashChain = extractHashChain(eventHash);
        IHash mergedHash = eventHash;
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

    private void visualize(HashNode node, int distFromRoot, StringBuilder sb, int hashStrLength) {
        if (node == null) {
            return;
        }
        if (distFromRoot == 1) {
            sb.append(node.getHash().toString(), 0, hashStrLength);
        }
        HashNode rightNode = node.getRightNode();
        if (rightNode != null) {
            sb.append("-").append(rightNode.getHash().toString(), 0, hashStrLength);
        } else {
            sb.append(String.format("%n"));
        }
        visualize(rightNode, distFromRoot + 1, sb, hashStrLength);

        HashNode leftNode = node.getLeftNode();
        if (leftNode != null) {
            for (int i = 0; i < distFromRoot * hashStrLength; i++) {
                sb.append(" ");
            }
            sb.append("\\");
            sb.append(leftNode.getHash().toString(), 0, hashStrLength);
        }
        visualize(leftNode, distFromRoot + 1, sb, hashStrLength);
    }

    public String visualize() throws Exception {
        String testHashStr = HashUtils.createHash("test", hashAlgorithm).toString();
        return visualize(testHashStr.length());
    }

    public String visualize(int hashStrLength) {
        StringBuilder sb = new StringBuilder();
        visualize(getRoot(), 1, sb, hashStrLength);
        return sb.toString();
    }

    static HashTreeBuilder builder() {
        return new HashTreeBuilder();
    }

    static HashTreeBuilder builder(String hashAlgorithm) {
        return new HashTreeBuilder(hashAlgorithm);
    }

    static class HashTreeBuilder {

        private HashTree hashTree;
        private boolean isBuilt = false;

        private HashTreeBuilder() {
            this(HashUtils.getDefaultHashAlgorithm());
        }
        private HashTreeBuilder(String hashAlgorithm){
            switch (OPERATION_MODE) {
                case CPU:
                    this.hashTree = new HashTreeCpuImpl(hashAlgorithm);
                    return;
                case MEM:
                    this.hashTree = new HashTreeMemImpl(hashAlgorithm);
                    return;
                default:
                    throw new IllegalArgumentException("Operation mode not recognized!");
            }
        }

        HashTreeBuilder appendEvent(String event) throws Exception {
            validateAction();
            hashTree.addNode(new HashLeaf(event, hashTree.hashAlgorithm));
            return this;
        }

        HashTreeBuilder mergeTree(HashTree treeToMerge) throws Exception {
            validateAction();
            hashTree.merge(treeToMerge);
            return this;
        }

        HashTree build() throws Exception {
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

    public enum OperationMode {
        MEM, CPU
    }
}

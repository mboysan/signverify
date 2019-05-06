package ee.mboysan.signverify.tree;

import ee.mboysan.signverify.exceptions.HashNotFoundException;
import ee.mboysan.signverify.exceptions.TreeConstructionFailedException;
import ee.mboysan.signverify.hashing.HashUtils;
import ee.mboysan.signverify.hashing.IHash;

import java.util.*;

/**
 * Merkle Tree / Binary Hash tree implementation.
 */
public abstract class HashTree {

    /**
     * Determines the operation mode of the tree.
     * @see HashTreeCpuImpl
     * @see HashTreeMemImpl
     * @see OperationMode
     */
    public static OperationMode OPERATION_MODE = OperationMode.MEM;

    /** Stack to keep track of the dangling nodes/branches. */
    private Deque<HashNode> nodeStack = new ArrayDeque<>();

    /** root of the tree. */
    private HashNode root;

    /** number of leaves/events. */
    private int leafCount = 0;

    /** hash algorithm for hashing operations. */
    private final String hashAlgorithm;

    HashTree(String hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }

    /**
     * adds a new node to the tree being built.
     */
    void addNode(HashNode node) throws Exception {
        if (node instanceof HashLeaf) {
            leafCount++;
        }
        if (nodeStack.isEmpty()) {
            nodeStack.push(node);
        } else {
            HashNode prevNode = nodeStack.pop();
            if (prevNode.getDepth() == node.getDepth()) {
                // merge nodes if they are of equal depth.
                HashNode parent = new HashNode(prevNode, node);
                addNode(parent);
            } else {
                // wait for equal depth node.
                nodeStack.push(prevNode);   // put it back
                nodeStack.push(node);
            }
        }
    }

    /**
     * merges two trees.
     */
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

    /**
     * constructs a hash tree from the {@link #nodeStack}.
     */
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

    /**
     * Finds a node that represents the given hash.
     * @param hash hash to search.
     * @return node representing the hash.
     * @throws HashNotFoundException if node not found.
     */
    abstract HashNode findNode(IHash hash) throws HashNotFoundException;

    /**
     * add hashes of the siblings starting from the leaf.
     */
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

    /**
     * Hashes the eventToCheck and calls {@link #extractHashChain(IHash)}.
     */
    public List<IHash> extractHashChain(String eventToCheck) throws HashNotFoundException, Exception {
        return extractHashChain(HashUtils.createHash(eventToCheck, hashAlgorithm));
    }

    /**
     * Extracts the hash chain for a given event.
     * @param eventHash leaf/node hash to extract hash chain for.
     * @return a list of hashes from leaf hash to root hash. [leafHash, [c1,[c2,...]], rootHash]
     * @throws HashNotFoundException if hash is not found.
     */
    public List<IHash> extractHashChain(IHash eventHash) throws HashNotFoundException {
        HashNode node = findNode(eventHash);
        List<IHash> hashes = new ArrayList<>();
        _extractHashChain(node, hashes);
        hashes.add(0, eventHash);   // include leaf's hash as the first element
        hashes.add(getRoot().getHash());   // include root's hash as the last element
        return hashes;
    }

    /**
     * Hashes the eventToCheck and calls {@link #isValidEvent(IHash)}.
     */
    public boolean isValidEvent(String eventToCheck) throws Exception {
        return isValidEvent(HashUtils.createHash(eventToCheck, hashAlgorithm));
    }

    /**
     * Checks if a given eventHash is a member of the hash tree.
     * @param eventHash hash of the event to check for validity.
     * @return true if event is included in the tree, false otherwise.
     * @throws Exception if hash not found or a problem occurs while checking for validity of the event.
     */
    public boolean isValidEvent(IHash eventHash) throws Exception {
        List<IHash> hashChain = extractHashChain(eventHash);
        if (hashChain == null || hashChain.get(0) == null || !hashChain.get(0).equals(eventHash)) {
            throw new HashNotFoundException("Hash is invalid: [" + eventHash + "]");
        }
        IHash mergedHash = hashChain.remove(0); // leaf's hash
        IHash rootHash = hashChain.remove(hashChain.size() - 1);
        for (IHash iHash : hashChain) {
            mergedHash = iHash.getPosition() == IHash.Position.RIGHT
                    ? HashUtils.mergeHashes(mergedHash, iHash)
                    : HashUtils.mergeHashes(iHash, mergedHash);
        }
        return getRoot().getHash().equals(rootHash) && getRoot().getHash().equals(mergedHash);
    }


    /**
     * @return see {@link #root}.
     */
    public HashNode getRoot() {
        return root;
    }

    /**
     * @return see {@link #leafCount}.
     */
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

    /**
     * @return a new hash tree builder with the default hash algorithm.
     */
    static HashTreeBuilder builder() {
        return builder(HashUtils.getDefaultHashAlgorithm());
    }

    /**
     * @return a new hash tree builder with the provided hash algorithm.
     */
    static HashTreeBuilder builder(String hashAlgorithm) {
        return new HashTreeBuilder(hashAlgorithm);
    }

    static class HashTreeBuilder {

        private HashTree hashTree;
        private boolean isBuilt = false;

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
        /** Memory intensive. */
        MEM,

        /** CPU intensive. */
        CPU
    }
}

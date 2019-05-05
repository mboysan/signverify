package ee.mboysan.signverify.tree;

import ee.mboysan.signverify.exceptions.HashNotFoundException;
import ee.mboysan.signverify.hashing.IHash;

import java.util.HashMap;
import java.util.Map;

class HashTreeMemImpl extends HashTree {

    private Map<String, HashNode> nodeMap = new HashMap<>();

    HashTreeMemImpl(String hashAlgorithm) {
        super(hashAlgorithm);
    }

    @Override
    void addNode(HashNode node) throws Exception {
        nodeMap.put(node.getHash().toString(), node);
        super.addNode(node);
    }

    @Override
    void merge(HashTree other) throws Exception {
        if (other instanceof HashTreeMemImpl) {
            _merge((HashTreeMemImpl) other);
        }
    }

    private void _merge(HashTreeMemImpl other) throws Exception {
        super.merge(other);
        this.nodeMap.putAll(other.nodeMap);
    }

    HashNode findNode(IHash hash) throws HashNotFoundException {
        HashNode node = nodeMap.get(hash.toString());
        if (node == null) {
            throw new HashNotFoundException("[" + hash + "]");
        }
        return node;
    }
}

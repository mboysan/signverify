package ee.mboysan.signverify.tree;

import ee.mboysan.signverify.exceptions.HashNotFoundException;
import ee.mboysan.signverify.hashing.IHash;

import java.util.Optional;

class HashTreeCpuImpl extends HashTree {


    HashTreeCpuImpl(String hashAlgorithm) {
        super(hashAlgorithm);
    }

    @Override
    HashNode findNode(IHash hash) throws HashNotFoundException {
        if (getRoot() == null) {
            throw new HashNotFoundException("[" + hash + "](1)");
        }
        return _findNode(getRoot(), hash).orElseThrow(() -> new HashNotFoundException("[" + hash + "](2)"));
    }

    private Optional<HashNode> _findNode(HashNode node, IHash hashToFind) {
        if (node == null) {
            return Optional.empty();
        }
        if (node.getHash().equals(hashToFind)) {
            return Optional.of(node);
        }
        Optional<HashNode> op1 = _findNode(node.getLeftNode(), hashToFind);
        Optional<HashNode> op2 = _findNode(node.getRightNode(), hashToFind);
        return op1.isPresent() ? op1 : op2;
    }
}

package tree;

public abstract class AbstractHashTreeTestBase {

    HashTree createHashTree(int size, String eventStrPrefix) throws Exception {
        HashTree.HashTreeBuilder treeBuilder = HashTree.builder();
        for (int i = 0; i < size; i++) {
            treeBuilder.appendEvent(eventStrPrefix + i);
        }
        return treeBuilder.build();
    }

}

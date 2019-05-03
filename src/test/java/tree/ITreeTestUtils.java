package tree;

import java.util.List;

import static org.junit.Assert.assertTrue;

interface ITreeTestUtils {

    static HashTree createHashTree(List<String> events) throws Exception {
        HashTree.HashTreeBuilder treeBuilder = HashTree.builder();
        for (String event : events) {
            treeBuilder.appendEvent(event);
        }
        return treeBuilder.build();
    }

    static void assertEventsValid(HashTree tree, List<String> events) throws Exception {
        for (String event : events) {
            assertEventValid(tree, event);
        }
    }

    static void assertEventValid(HashTree tree, String event) throws Exception {
        assertTrue(tree.isValidEvent(event));
    }

}

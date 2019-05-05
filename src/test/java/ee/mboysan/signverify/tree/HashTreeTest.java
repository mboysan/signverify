package ee.mboysan.signverify.tree;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static ee.mboysan.signverify.tree.ITreeTestUtils.createHashTree;
import static ee.mboysan.signverify.util.TestUtils.createEvents;

public class HashTreeTest {

    @Test
    public void testOneElementTreeConstruction() throws Exception {
        HashTree hashTree = HashTree.builder()
                .appendEvent("event1")
                .build();
        assertNotNull(hashTree.getRoot().getHash());
    }

    @Test
    public void testSmallTreeConstruction() throws Exception {
        HashTree hashTree = HashTree.builder()
                .appendEvent("event1")
                .appendEvent("event2")
                .build();
        assertNotNull(hashTree.getRoot().getHash());
    }

    @Test
    public void testLargeBalancedTreeConstruction() throws Exception {
        HashTree hashTree = createHashTree(createEvents(8, "event"));
        assertNotNull(hashTree.getRoot().getHash());
    }

    @Test
    public void testLargeUnBalancedTreeConstruction() throws Exception {
        HashTree hashTree = createHashTree(createEvents(11, "event"));
        assertNotNull(hashTree.getRoot().getHash());
    }

    @Test
    public void testTreeVisualizationOnBalancedTree() throws Exception {
        HashTree hashTree = createHashTree(createEvents(8, "event"));
        System.out.println("HashTreeTest.testTreeVisualizationOnBalancedTree()");
        System.out.println(hashTree.visualize());
    }

    @Test
    public void testTreeVisualizationOnUnbalancedTree() throws Exception {
        HashTree hashTree = createHashTree(createEvents(11, "event"));
        System.out.println("HashTreeTest.testTreeVisualizationOnUnbalancedTree()");
        System.out.println(hashTree.visualize());
    }
}
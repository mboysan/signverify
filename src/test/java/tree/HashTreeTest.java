package tree;

import org.junit.Test;
import tree.HashTree.HashTreeBuilder;

import static org.junit.Assert.assertNotNull;

public class HashTreeTest {

    @Test
    public void testOneElementTreeConstruction() {
        HashTree hashTree = HashTree.builder()
                .appendEvent("event1")
                .build();
        assertNotNull(hashTree.getRoot());
    }

    @Test
    public void testSmallTreeConstruction() {
        HashTree hashTree = HashTree.builder()
                .appendEvent("event1")
                .appendEvent("event2")
                .build();
        assertNotNull(hashTree.getRoot());
    }

    @Test
    public void testLargeBalancedTreeConstruction() {
        HashTreeBuilder treeBuilder = HashTree.builder();
        for (int i = 0; i < 8; i++) {
            treeBuilder.appendEvent("event" + i);
        }
        HashTree hashTree = treeBuilder.build();
        assertNotNull(hashTree.getRoot());
    }

    @Test
    public void testLargeUnBalancedTreeConstruction() {
        HashTreeBuilder treeBuilder = HashTree.builder();
        for (int i = 0; i < 11; i++) {
            treeBuilder.appendEvent("event" + i);
        }
        HashTree hashTree = treeBuilder.build();
        assertNotNull(hashTree.getRoot());
    }

}
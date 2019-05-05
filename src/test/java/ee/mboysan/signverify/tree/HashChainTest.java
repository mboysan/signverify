package ee.mboysan.signverify.tree;

import ee.mboysan.signverify.exceptions.HashNotFoundException;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static ee.mboysan.signverify.tree.ITreeTestUtils.assertEventsValid;
import static ee.mboysan.signverify.tree.ITreeTestUtils.createHashTree;
import static ee.mboysan.signverify.util.TestUtils.createEvents;

public class HashChainTest {

    @Test
    public void testHashValidationSuccessOnOneElementTree() throws Exception {
        HashTree hashTree = HashTree.builder().appendEvent("event0").build();
        assertTrue(hashTree.isValidEvent("event0"));
    }

    @Test(expected = HashNotFoundException.class)
    public void testHashValidationFailureOnOneElementTree() throws Exception {
        HashTree hashTree = HashTree.builder().appendEvent("event0").build();
        hashTree.isValidEvent("non-existing-event");
    }

    @Test
    public void testHashValidationSuccessOnBalancedTree() throws Exception {
        List<String> events = createEvents(8, "event");
        HashTree hashTree = createHashTree(events);
        assertEventsValid(hashTree, events);
    }

    @Test(expected = HashNotFoundException.class)
    public void testHashValidationFailureOnBalancedTree() throws Exception {
        HashTree hashTree = createHashTree(createEvents(8, "event"));
        hashTree.isValidEvent("non-existent-event");
    }

    @Test
    public void testHashValidationSuccessOnUnbalancedTree() throws Exception {
        List<String> events = createEvents(11, "event");
        HashTree hashTree = createHashTree(events);
        assertEventsValid(hashTree, events);
    }

    @Test(expected = HashNotFoundException.class)
    public void testHashValidationFailureOnUnbalancedTree() throws Exception {
        HashTree hashTree = createHashTree(createEvents(11, "event"));
        hashTree.isValidEvent("non-existent-event");
    }
}

package tree;

import exceptions.HashNotFoundException;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class HashChainTest extends AbstractHashTreeTestBase {
    @Test
    public void testHashValidationSuccessOnBalancedTree() throws Exception {
        HashTree hashTree = createHashTree(createEvents(8, "event"));
        assertTrue(hashTree.isValidEvent("event0"));
        assertTrue(hashTree.isValidEvent("event1"));
        assertTrue(hashTree.isValidEvent("event6"));
        assertTrue(hashTree.isValidEvent("event7"));
    }

    @Test(expected = HashNotFoundException.class)
    public void testHashValidationFailureOnBalancedTree() throws Exception {
        HashTree hashTree = createHashTree(createEvents(8, "event"));
        hashTree.isValidEvent("non-existent-event");
    }

    @Test
    public void testHashValidationSuccessOnUnbalancedTree() throws Exception {
        HashTree hashTree = createHashTree(createEvents(11, "event"));
        assertTrue(hashTree.isValidEvent("event0"));
        assertTrue(hashTree.isValidEvent("event1"));
        assertTrue(hashTree.isValidEvent("event9"));
        assertTrue(hashTree.isValidEvent("event10"));
    }

    @Test(expected = HashNotFoundException.class)
    public void testHashValidationFailureOnUnbalancedTree() throws Exception {
        HashTree hashTree = createHashTree(createEvents(11, "event"));
        hashTree.isValidEvent("non-existent-event");
    }
}

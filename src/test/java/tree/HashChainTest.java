package tree;

import exceptions.HashNotFoundException;
import org.junit.Assert;
import org.junit.Test;

public class HashChainTest extends AbstractHashTreeTestBase {
    @Test
    public void testHashValidationSuccessOnBalancedTree() throws Exception {
        HashTree hashTree = createHashTree(8, "event");
        Assert.assertTrue(hashTree.isValidEvent("event0"));
        Assert.assertTrue(hashTree.isValidEvent("event1"));
        Assert.assertTrue(hashTree.isValidEvent("event6"));
        Assert.assertTrue(hashTree.isValidEvent("event7"));
    }

    @Test(expected = HashNotFoundException.class)
    public void testHashValidationFailureOnBalancedTree() throws Exception {
        HashTree hashTree = createHashTree(8, "event");
        hashTree.isValidEvent("non-existent-event");
    }

    @Test
    public void testHashValidationSuccessOnUnbalancedTree() throws Exception {
        HashTree hashTree = createHashTree(11, "event");
        Assert.assertTrue(hashTree.isValidEvent("event0"));
        Assert.assertTrue(hashTree.isValidEvent("event1"));
        Assert.assertTrue(hashTree.isValidEvent("event9"));
        Assert.assertTrue(hashTree.isValidEvent("event10"));
    }

    @Test(expected = HashNotFoundException.class)
    public void testHashValidationFailureOnUnbalancedTree() throws Exception {
        HashTree hashTree = createHashTree(11, "event");
        hashTree.isValidEvent("non-existent-event");
    }
}

package tree;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;

abstract class AbstractHashTreeTestBase {

    HashTree createHashTree(List<String> events) throws Exception {
        HashTree.HashTreeBuilder treeBuilder = HashTree.builder();
        for (String event : events) {
            treeBuilder.appendEvent(event);
        }
        return treeBuilder.build();
    }

    List<String> createEvents(int size, String eventStrPrefix) {
        return Stream.iterate(0, i -> i + 1)
                .limit(size)
                .map(i -> eventStrPrefix + i)
                .collect(Collectors.toList());
    }

    @SafeVarargs
    final List<String> flatten(List<String>... events) {
        return Stream.of(events).flatMap(Collection::stream).collect(Collectors.toList());
    }

    void isEventsValid(HashTree tree, List<String> events) throws Exception {
        for (String event : events) {
            assertTrue(tree.isValidEvent(event));
        }
    }

}

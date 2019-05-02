package tree;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;

public abstract class AbstractHashTreeTestBase {

    HashTree createHashTree(int size, String eventStrPrefix) throws Exception {
        HashTree.HashTreeBuilder treeBuilder = HashTree.builder();
        for (int i = 0; i < size; i++) {
            treeBuilder.appendEvent(eventStrPrefix + i);
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

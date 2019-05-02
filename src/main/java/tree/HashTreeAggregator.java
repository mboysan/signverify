package tree;

import hashing.IHash;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class HashTreeAggregator implements AutoCloseable {

    private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final List<Future<HashTree>> hashTreeFutures = new ArrayList<>();

    private HashTree aggregatedTree = null;

    public HashTreeAggregator aggregateEvents(List<String> events) {
        Future<HashTree> f = executor.submit(new TreeBuildJob(events));
        hashTreeFutures.add(f);
        return this;
    }

    public IHash endAndGetRootHash() throws Exception {
        HashTree.HashTreeBuilder treeBuilder = HashTree.builder();
        treeBuilder.mergeTree(aggregatedTree);
        for (Future<HashTree> htf : hashTreeFutures) {
            treeBuilder.mergeTree(htf.get());
        }
        aggregatedTree = treeBuilder.build();
        hashTreeFutures.clear();
        return aggregatedTree.getRoot().getHash();
    }

    HashTree getAggregatedTree() {
        return aggregatedTree;
    }

    @Override
    public void close() {
        executor.shutdown();
    }

    private class TreeBuildJob implements Callable<HashTree> {
        private final List<String> events;

        private TreeBuildJob(List<String> events) {
            this.events = events;
        }

        @Override
        public HashTree call() throws Exception {
            HashTree.HashTreeBuilder tb = HashTree.builder();
            for (String event : events) {
                tb.appendEvent(event);
            }
            return tb.build();
        }
    }

}

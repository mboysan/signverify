package ee.mboysan.signverify.tree;

import ee.mboysan.signverify.hashing.HashUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class HashTreeAggregator implements AutoCloseable {

    private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final List<Future<HashTree>> hashTreeFutures = new ArrayList<>();

    private final String hashAlgorithm;
    private HashTree aggregatedTree = null;

    public HashTreeAggregator() {
        this(HashUtils.getDefaultHashAlgorithm());
    }

    public HashTreeAggregator(String hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }

    public HashTreeAggregator aggregateEvents(List<String> events) {
        Future<HashTree> f = executor.submit(new TreeBuildJob(events));
        hashTreeFutures.add(f);
        return this;
    }

    public HashTreeAggregator endAggregation() throws Exception {
        HashTree.HashTreeBuilder treeBuilder = HashTree.builder(hashAlgorithm);
        treeBuilder.mergeTree(aggregatedTree);
        for (Future<HashTree> htf : hashTreeFutures) {
            HashTree ht = htf.get();
            treeBuilder.mergeTree(ht);
        }
        aggregatedTree = treeBuilder.build();
        hashTreeFutures.clear();
        return this;
    }

    public HashTree getAggregatedTree() {
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
            HashTree.HashTreeBuilder tb = HashTree.builder(hashAlgorithm);
            for (String event : events) {
                tb.appendEvent(event);
            }
            return tb.build();
        }
    }

}

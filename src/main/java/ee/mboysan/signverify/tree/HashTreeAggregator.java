package ee.mboysan.signverify.tree;

import ee.mboysan.signverify.hashing.HashUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Builds and aggregates trees.
 * <b>NB! </b> not thread safe. Protect it on your own.
 */
public class HashTreeAggregator implements AutoCloseable {

    private final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final List<Future<HashTree>> hashTreeFutures = new ArrayList<>();

    private final String hashAlgorithm;

    /** the final tree representing all the merged trees */
    private HashTree aggregatedTree = null;

    public HashTreeAggregator() {
        this(HashUtils.getDefaultHashAlgorithm());
    }

    public HashTreeAggregator(String hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }

    /**
     * Creates a tree build job with the given list of events.
     * @param events list of events for building a single tree.
     * @return this
     */
    public HashTreeAggregator aggregateEvents(List<String> events) {
        Future<HashTree> f = executor.submit(new TreeBuildJob(events));
        hashTreeFutures.add(f);
        return this;
    }

    /**
     * Ends the current tree build jobs, collects the built trees and merges them. Note that you can still continue
     * using {@link #aggregateEvents(List)} after calling this method.
     * @return this
     * @throws Exception if aggregation fails.
     */
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

    /**
     * @return see {@link #aggregatedTree}.
     */
    public HashTree getAggregatedTree() {
        return aggregatedTree;
    }

    @Override
    public void close() {
        // stop accepting new requests.
        executor.shutdown();
    }

    /**
     * Represents a job for building a single tree from the provided events.
     */
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

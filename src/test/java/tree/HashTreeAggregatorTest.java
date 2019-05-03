package tree;

import exceptions.HashNotFoundException;
import exceptions.TreeConstructionFailedException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static tree.ITreeTestUtils.assertEventValid;
import static tree.ITreeTestUtils.assertEventsValid;
import static util.TestUtils.createEvents;
import static util.TestUtils.flatten;

public class HashTreeAggregatorTest {

    @Test
    public void testAggregateNullEvents() throws Exception {
        try(HashTreeAggregator aggr = new HashTreeAggregator()) {
            aggr.aggregateEvents(null);
            aggr.endAggregation();
            fail("Should have failed creating the tree!");
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof NullPointerException);
        }
    }

    @Test
    public void testAggregateEmptyEvents() throws Exception {
        try(HashTreeAggregator aggr = new HashTreeAggregator()) {
            aggr.aggregateEvents(new ArrayList<>());
            aggr.endAggregation();
            fail("Should have failed creating the tree!");
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof TreeConstructionFailedException);
        }
    }

    @Test
    public void testAggregateEmptyStringEvents() throws Exception {
        try(HashTreeAggregator aggr = new HashTreeAggregator()) {
            aggr.aggregateEvents(Stream.of("").collect(Collectors.toList()));
            aggr.endAggregation();
            assertEventValid(aggr.getAggregatedTree(), "");
        }
    }

    @Test
    public void testAggregateSuccessSimple() throws Exception {
        List<String> events = createEvents(4, "event");
        multiRunAggregation(events, events);
    }

    @Test(expected = HashNotFoundException.class)
    public void testAggregateFailureSimple() throws Exception {
        List<String> events = createEvents(4, "event");
        multiRunAggregation("non-existing-event", events);
    }

    @Test
    public void testAggregateSuccessMultipleSetsOfEvents() throws Exception {
        List<String> events1 = createEvents(4, "event1.");
        List<String> events2 = createEvents(5, "event2.");
        List<String> events3 = createEvents(6, "event3.");

        List<String> eventsToCheck = flatten(events1, events2, events3);
        multiRunAggregation(eventsToCheck, events1, events2, events3);
    }

    @Test(expected = HashNotFoundException.class)
    public void testAggregateFailureMultipleSetsOfEvents() throws Exception {
        List<String> events1 = createEvents(4, "event1.");
        List<String> events2 = createEvents(7, "event2.");
        multiRunAggregation("non-existing-event", events1, events2);
    }


    @SafeVarargs
    private final void multiRunAggregation(String eventToCheck, List<String>... eventsToAggregate) throws Exception {
        multiRunAggregation(Stream.of(eventToCheck).collect(Collectors.toList()), eventsToAggregate);
    }

    @SafeVarargs
    private final void multiRunAggregation(List<String> eventsToValidate, List<String>... eventsToAggregate) throws Exception {
        int runCount = 10;
        for (int i = 0; i < runCount; i++) {
            try(HashTreeAggregator aggr = new HashTreeAggregator()) {
                for (List<String> events : eventsToAggregate) {
                    aggr.aggregateEvents(events);
                }
                aggr.endAggregation();
                assertEventsValid(aggr.getAggregatedTree(), eventsToValidate);
            }
        }
    }

}
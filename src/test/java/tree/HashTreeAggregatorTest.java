package tree;

import exceptions.HashNotFoundException;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HashTreeAggregatorTest extends AbstractHashTreeTestBase {

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
                isEventsValid(aggr.getAggregatedTree(), eventsToValidate);
            }
        }
    }

}
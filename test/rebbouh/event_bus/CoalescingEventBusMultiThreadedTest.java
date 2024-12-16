package rebbouh.event_bus;

import com.rebbouh.event_bus.CoalescingEventBusMultiThreaded;
import com.rebbouh.event_bus.Consumer;
import com.rebbouh.event_bus.EventBusMultiThreaded;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CoalescingEventBusMultiThreadedTest {
    private static final int NUM_THREADS = 3;
    private CoalescingEventBusMultiThreaded eventBus;
    
    static class TestConsumer implements Consumer<String> {
        List<String> events = new ArrayList<>();

        @Override
        public void consume(String event) throws Exception {
            events.add(event);
        }
    }

    @BeforeEach
    void setUp() {
        eventBus = new CoalescingEventBusMultiThreaded();
    }

    @Test
    void testEventDeliveryToSingleConsumer() throws InterruptedException {
        TestConsumer consumer = new TestConsumer();
        eventBus.addSubscriber(String.class, consumer);

        // Publish events
        eventBus.publishEvent("Event 1");
        // Give threads time to process events
        Thread.sleep(10);
        eventBus.publishEvent("Will be overridden !");
        eventBus.publishEvent("The latest event");

        // Give threads time to process events
        Thread.sleep(100);

        // Assert that all events are received by the consumer
        assertEquals(2, consumer.events.size());
        assertTrue(consumer.events.containsAll(List.of("Event 1", "The latest event")));
    }

}

package rebbouh.event_bus;

import com.rebbouh.event_bus.Consumer;
import com.rebbouh.event_bus.EventBusMultiThreaded;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EventBusMultiThreadedTest {
    private static final int NUM_THREADS = 3;
    private EventBusMultiThreaded<String> eventBus;
    
    static class TestConsumer implements Consumer<String> {
        List<String> events = new ArrayList<>();

        @Override
        public void consume(String event) throws Exception {
            events.add(event);
        }
    }

    @BeforeEach
    void setUp() {
        eventBus = new EventBusMultiThreaded<>();
    }

    @Test
    void testEventDeliveryToSingleConsumer() throws InterruptedException {
        TestConsumer consumer = new TestConsumer();
        eventBus.subscribe(consumer);

        // Publish events
        eventBus.publishEvent("Event 1");
        eventBus.publishEvent("Event 2");
        eventBus.publishEvent("Event 3");

        // Give threads time to process events
        Thread.sleep(100);

        // Assert that all events are received by the consumer
        assertEquals(3, consumer.events.size());
        assertTrue(consumer.events.containsAll(List.of("Event 1", "Event 2", "Event 3")));
    }

    @Test
    void testMultipleConsumersReceiveEvents() throws InterruptedException {
        TestConsumer consumer1 = new TestConsumer();
        TestConsumer consumer2 = new TestConsumer();

        eventBus.subscribe(consumer1);
        eventBus.subscribe(consumer2);

        // Publish events
        eventBus.publishEvent("Event 1");
        eventBus.publishEvent("Event 2");

        // Give threads time to process events
        Thread.sleep(100);

        // Assert both consumers received the events
        assertEquals(2, consumer1.events.size());
        assertEquals(2, consumer2.events.size());

        assertTrue(consumer1.events.containsAll(List.of("Event 1", "Event 2")));
        assertTrue(consumer2.events.containsAll(List.of("Event 1", "Event 2")));
    }

    @Test
    void testMultiplePublishersAndConsumers() throws InterruptedException {
        TestConsumer consumer = new TestConsumer();
        eventBus.subscribe(consumer);
        // Publisher 1
        Thread publisher1 = new Thread(() -> {
            for (var i = 0; i < 5; i++) {
                eventBus.publishEvent("Event from publisher 1: %d".formatted(i));
            }
        });
        // Publisher 2
        Thread publisher2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                eventBus.publishEvent("Event from publisher 2: %d".formatted(i));
            }
        });
        // Can be done with executor service, threads are used here to join after the execution ends.
        publisher1.start();
        publisher2.start();
        publisher1.join();
        publisher2.join();

        // Give threads time to process events
        Thread.sleep(100);

        // Assert that all events from both publishers are received
        assertEquals(10, consumer.events.size());
        for (int i = 0; i < 5; i++) {
            assertTrue(consumer.events.contains("Event from publisher 1: " + i));
            assertTrue(consumer.events.contains("Event from publisher 2: " + i));
        }
    }

}

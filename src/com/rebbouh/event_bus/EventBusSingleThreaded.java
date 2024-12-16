package com.rebbouh.event_bus;

import java.util.ArrayList;
import java.util.List;

public class EventBusSingleThreaded<T> {

    private final List<Consumer<T>> consumers = new ArrayList<>();

    // This will protect the consumers from a rogue one that
    // that throws an exception and makes the event vanish. (all consumers receive the event regardless of the execution
    // result of the previous consumer.
    public void publishEvent(T event) {
        consumers.forEach(consumer -> {
            try {
                consumer.consume(event);
                System.out.println("EventBusSingleThreaded consuming: " + event);
            } catch (Exception e) {
                // Catch all exceptions to avoid execution flow interruption.
                System.out.println(e.getMessage());
            }
        });
    }

    /**
     * Adds the consumer to the subscribers of this EventBus.
     *
     * @param consumer The consumer
     */
    public void subscribe(Consumer<T> consumer) {
        consumers.add(consumer);
    }
}

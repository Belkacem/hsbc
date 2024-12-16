package com.rebbouh.event_bus;

import java.util.*;
import java.util.concurrent.*;


// This multi-threaded event bus is making use of the executor service internal structures to avoid handling queues
public class EventBusMultiThreaded<T> {
    // We take advantage of the executors service queue to avoid handling a blocking queue.
    private final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private final List<Consumer<T>> consumers = Collections.synchronizedList(new ArrayList<>());

    public EventBusMultiThreaded() {
    }

    /**
     * @param event The event to be published
     */
    public synchronized void publishEvent(T event) {
        consumers.stream().map(consumer -> (Runnable) () -> {
            try {
                consumer.consume(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        })  // We can map this to Futures to emulate a fork join pool / event.
            //.map(executorService::submit).toList(); then we wait on the futures to complete.
            .forEach(executorService::submit);
    }

    public synchronized void subscribe(Consumer<T> consumer) {
        consumers.add(consumer);
    }

}

package com.rebbouh.event_bus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

// This Single threaded event bus is making use of the executor sevrice internal structures to avoid handling queues
// This is a more fun implementation :)
public class FunEventBusSingleThreaded<T> {
    // The single threaded bus should be a singleton to avoid multiple initializations
    private static final FunEventBusSingleThreaded BUS = new FunEventBusSingleThreaded();

    // We take advantage of the executors service queue to avoid handling a blocking queue.
    private final ExecutorService executorService = Executors.newWorkStealingPool();
    private final List<Consumer> consumers = new ArrayList<>();

    // Private Constructor / ! \ do not alter / ! \
    private FunEventBusSingleThreaded() {
    }

    public static final <TYPE> FunEventBusSingleThreaded<TYPE> getBus() {
        return (FunEventBusSingleThreaded<TYPE>) BUS;
    }

    //  Publishing the event is done for each consumer separately, this will protect the consumers from a rogue one that
    // that throws an exception and makes the event vanish. (all consumers receive the event regardless of the execution
    // result of the previous consumer.
    public void publishEvent(T event) throws InterruptedException, ExecutionException {
        // Here we submit the callables directly on the executor service to avoid consuming on the same execution flow
        var futures = executorService.invokeAll(consumers.stream()
                .map(consumer -> (Callable<T>) () -> {
                    consumer.consume(event);
                    System.out.println("FunEventBusSingleThreaded consuming: " + event);
                    return event;
                }).toList());
        for (Future<T> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void subscribe(Consumer<T> consumer) {
        consumers.add(consumer);
    }
}

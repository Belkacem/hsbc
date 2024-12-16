package com.rebbouh.event_bus;

import javax.swing.text.html.Option;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;


/**
 * For this Event bus we cant use the executor service internal blocking queue to manage queuing events, we have to :
 * 1) Write an event bus that takes the
 */

public class CoalescingEventBusMultiThreaded implements EventBus, Runnable {
  private final Set<ConsumerWithFilter<Object>> consumersAndFilters = ConcurrentHashMap.newKeySet();
  private final BlockingQueue<Object> eventQueue = new CoalescingQueue<>();

  private final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

  public CoalescingEventBusMultiThreaded() {
    Executors.newSingleThreadExecutor().submit(this);
  }

  @Override
  public void publishEvent(Object event) {
    eventQueue.offer(event);
  }

  @Override
  public void addSubscriber(Class<?> clazz, Consumer<?> consumer) {
    Objects.requireNonNull(clazz);
    Predicate<Object> predicate = event -> event.getClass() == clazz;
    consumersAndFilters.add(new ConsumerWithFilter(consumer, Optional.of(predicate)));
  }

  @Override
  public void addSubscriberForFilteredEvents(Consumer<?> consumer, Predicate<Object> filter) {
    consumersAndFilters.add(new ConsumerWithFilter(consumer, Optional.ofNullable(filter)));
  }

  @Override
  public void run() {
    while (true) {
      try {
        var event = eventQueue.take();
        consumersAndFilters.stream()
            .filter(cwf -> cwf.filter.isEmpty() || cwf.filter.get().test(event))
            .map(ConsumerWithFilter::consumer)
            .map(consumer -> (Runnable) () -> {
              try {
                consumer.consume(event);
              } catch (Exception e) {
                e.printStackTrace();
              }
            })
            .forEach(executorService::submit);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private record ConsumerWithFilter<T>(Consumer<T> consumer, Optional<Predicate<T>> filter) {
  }

}

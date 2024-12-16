package com.rebbouh.sws;

import com.rebbouh.event_bus.Consumer;

import java.util.function.Predicate;

public interface SlidingWindowStatistics {

  /**
   * Add a measurement, restricted the value to ints to avoid complexity for map key usage
   */
  void add(int measurement);

  /**
   * Subscriptions uses {@link com.rebbouh.event_bus.CoalescingEventBusMultiThreaded} under the hood.
   * @param consumer the consumer object.
   * @param filter the filter if needed.
   */
  void subscribeForStatistics(Consumer consumer, Predicate filter);

  /**
   * Retrurs the latest snapshot of statistics.
   */
  Statistics getLatestStatistics();
}
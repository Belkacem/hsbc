package com.rebbouh.sws;

import com.rebbouh.event_bus.CoalescingEventBusMultiThreaded;
import com.rebbouh.event_bus.Consumer;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class SlidingWindowStatisticsImpl implements SlidingWindowStatistics {
  /**
   * This is the event bus used to serve the statistics to consumers.
   */
  private final CoalescingEventBusMultiThreaded eventBus = new CoalescingEventBusMultiThreaded();
  /**
   * This data structure holds the counts for each measurement, it's used for mode computing.
   */
  private final TreeMap<MeasurementCount, MeasurementCount> counts = new TreeMap<>(Comparator.comparingInt(m -> m.count));
  /**
   * this is the priority queue that contains all the measurement, it's used to compute the percentiles, and also used
   * for size and to compute the sum, which is needed for mean.
   */
  private final PriorityQueue<Integer> ranks = new PriorityQueue<>();
  private int sum = 0;

  private final int windowSize;
  private volatile double mean;
  private volatile int mode;
  private volatile int[] percentiles;

  public SlidingWindowStatisticsImpl(int windowSize) {
    this.windowSize = windowSize;
  }

  public SlidingWindowStatisticsImpl() {
    this.windowSize = Integer.MAX_VALUE;
  }

  /**
   * This method adjusts all the stats using eht added measurement and the removed one.
   */
  private synchronized void adjustStatistics(Integer measurement, Integer removedMeasurement) {
    var ranksSize = ranks.size();
    this.sum = sum + Objects.requireNonNullElse(measurement, 0) - Objects.requireNonNullElse(removedMeasurement, 0);
    // compute the mean.
    this.mean = (double) this.sum / ranksSize;
    // compute the percentiles.
    var ranksArray = ranks.toArray(new Integer[ranksSize]);
    this.percentiles = IntStream.range(1, 100)
        .map(pctile -> (int) Math.ceil(pctile / 100.0 * ranksSize) - 1)
        .map(index -> ranksArray[index])
        .toArray();
    // get the mode, it's the firest one as the maps sorts the meansurement using the count comparator (cf see stating initializer)
    this.mode = counts.firstKey().measurement;
  }

  @Override
  public synchronized void add(int measurement) {
    Integer removedMeasurement = null;
    if (ranks.size() >= windowSize) {
      removedMeasurement = ranks.poll();
    }
    ranks.offer(measurement);
    // counts structure Add count for the new measurement
    MeasurementCount initialMeasurementCount = new MeasurementCount(measurement, 0);
    MeasurementCount measurementCount = counts.getOrDefault(initialMeasurementCount, initialMeasurementCount);
    MeasurementCount newMeasurementCount = new MeasurementCount(measurement, measurementCount.count + 1);
    counts.put(newMeasurementCount, newMeasurementCount);
    // decrement count for the remove measurement
    if (removedMeasurement != null) {
      MeasurementCount removedMeasurementCount = counts.get(new MeasurementCount(removedMeasurement, 0));
      MeasurementCount newRemovedMeasurementCount = new MeasurementCount(removedMeasurement, removedMeasurementCount.count - 1);
      counts.compute(newRemovedMeasurementCount, (k, v) -> newRemovedMeasurementCount.count > 0 ? newRemovedMeasurementCount : null);
    }
    adjustStatistics(measurement, removedMeasurement);
    eventBus.publishEvent(new StatisticsImpl(this.mean, this.mode, this.percentiles));
  }


  @Override
  public void subscribeForStatistics(Consumer consumer, Predicate filter) {
    eventBus.addSubscriberForFilteredEvents(consumer, filter);
  }

  @Override
  public Statistics getLatestStatistics() {
    return new StatisticsImpl(this.mean, this.mode, this.percentiles);
  }

  private record MeasurementCount(int measurement, int count) {
    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      MeasurementCount that = (MeasurementCount) o;
      return measurement == that.measurement;
    }

    @Override
    public int hashCode() {
      return Objects.hash(measurement);
    }
  }
}
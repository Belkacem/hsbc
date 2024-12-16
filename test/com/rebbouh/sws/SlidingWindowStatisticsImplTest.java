package com.rebbouh.sws;

import com.rebbouh.event_bus.CoalescingEventBusMultiThreaded;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SlidingWindowStatisticsImplTest {

  private SlidingWindowStatistics slidingWindowStatistics;
  private Statistics currentStats = null;

  @BeforeEach
  void setUp() {
    slidingWindowStatistics = new SlidingWindowStatisticsImpl();
  }

  @Test
  void subscribeForStatistics() throws InterruptedException {
    slidingWindowStatistics.subscribeForStatistics(event -> this.currentStats = (Statistics) event, null);
    slidingWindowStatistics.add(2);
    slidingWindowStatistics.add(2);
    slidingWindowStatistics.add(2);
    slidingWindowStatistics.add(2);
    slidingWindowStatistics.add(4);
    slidingWindowStatistics.add(3);
    Thread.sleep(100);
    assertEquals(currentStats.getMean(), 2.5);
    assertEquals(currentStats.getMode(), 2);
    assertEquals(currentStats.getPctile(50), 2);
  }

  @Test
  void getLatestStatistics() {
    slidingWindowStatistics.add(2);
    slidingWindowStatistics.add(2);
    slidingWindowStatistics.add(2);
    slidingWindowStatistics.add(2);
    slidingWindowStatistics.add(4);
    slidingWindowStatistics.add(3);
    Statistics latestStatistics = slidingWindowStatistics.getLatestStatistics();
    assertEquals(2.5, latestStatistics.getMean());
    assertEquals(2, latestStatistics.getMode());
    assertEquals(latestStatistics.getPctile(60), 2);


  }
}
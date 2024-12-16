package com.rebbouh.sws;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    slidingWindowStatistics.add(4);
    slidingWindowStatistics.add(4);
    slidingWindowStatistics.add(4);
    slidingWindowStatistics.add(4);
    slidingWindowStatistics.add(3);
    Statistics latestStatistics = slidingWindowStatistics.getLatestStatistics();
    assertEquals(3.1, latestStatistics.getMean());
    assertEquals(4, latestStatistics.getMode());
    assertEquals(latestStatistics.getPctile(60), 4);


  }

  @Test
  void getLatestStatisticsWithRestrictedWindow() {

    var boundSlidingWindowStatistics = new SlidingWindowStatisticsImpl(5);
    boundSlidingWindowStatistics.add(2);
    boundSlidingWindowStatistics.add(2);
    boundSlidingWindowStatistics.add(2);
    boundSlidingWindowStatistics.add(2);
    boundSlidingWindowStatistics.add(4);
    Statistics latestStatistics = boundSlidingWindowStatistics.getLatestStatistics();
    assertEquals(2.4, latestStatistics.getMean());
    assertEquals(2, latestStatistics.getMode());
    assertEquals(latestStatistics.getPctile(10), 2);

    boundSlidingWindowStatistics.add(4);
    boundSlidingWindowStatistics.add(4);
    boundSlidingWindowStatistics.add(4);
    boundSlidingWindowStatistics.add(4);
    latestStatistics = boundSlidingWindowStatistics.getLatestStatistics();
    assertEquals(4, latestStatistics.getMean());
    assertEquals(4, latestStatistics.getMode());
    assertEquals(latestStatistics.getPctile(60), 4);


  }
}
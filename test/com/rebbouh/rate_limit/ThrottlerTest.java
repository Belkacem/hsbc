package com.rebbouh.rate_limit;

import com.rebbouh.event_bus.CoalescingEventBusMultiThreaded;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class ThrottlerTest {
  private Throttler throttler;
  private final AtomicBoolean processorCalled = new AtomicBoolean(false);

  @BeforeEach
  void setUp() {
    throttler = new Throttler(2000, 5);
    processorCalled.set(false);
  }

  @Test
  void shouldProceed() throws InterruptedException {
    assertEquals(ThrottleResult.PROCEED, throttler.shouldProceed());
    Thread.sleep(1000);
    assertEquals(ThrottleResult.PROCEED, throttler.shouldProceed());
    assertEquals(ThrottleResult.PROCEED, throttler.shouldProceed());
    assertEquals(ThrottleResult.PROCEED, throttler.shouldProceed());
    assertEquals(ThrottleResult.PROCEED, throttler.shouldProceed());
    assertEquals(ThrottleResult.DO_NOT_PROCEED, throttler.shouldProceed());
    Thread.sleep(1000);
    assertEquals(ThrottleResult.PROCEED, throttler.shouldProceed());
    assertEquals(ThrottleResult.DO_NOT_PROCEED, throttler.shouldProceed());
    Thread.sleep(1000);
    assertEquals(ThrottleResult.PROCEED, throttler.shouldProceed());
    assertEquals(ThrottleResult.PROCEED, throttler.shouldProceed());
    assertEquals(ThrottleResult.PROCEED, throttler.shouldProceed());
    assertEquals(ThrottleResult.PROCEED, throttler.shouldProceed());
  }

  @Test
  void notifyWhenCanProceed() throws InterruptedException {
    assertEquals(ThrottleResult.PROCEED, throttler.shouldProceed());
    assertEquals(ThrottleResult.PROCEED, throttler.shouldProceed());
    assertEquals(ThrottleResult.PROCEED, throttler.shouldProceed());
    assertEquals(ThrottleResult.PROCEED, throttler.shouldProceed());
    assertEquals(ThrottleResult.PROCEED, throttler.shouldProceed());
    // we could use mockito to assert that the method proceed of Processor was called.
    // instead i'll use an atomic boolean.
    Processor processor = () -> {
      assertFalse(processorCalled.getAndSet(true));
    };
    throttler.notifyWhenCanProceed(processor);
    Thread.sleep(2100);
    assertTrue(processorCalled.get());
  }
}
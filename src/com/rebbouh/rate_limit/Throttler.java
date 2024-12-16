package com.rebbouh.rate_limit;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;

public class Throttler implements ThrottlerInterface {

  private final ConcurrentLinkedQueue<Long> requestTimes = new ConcurrentLinkedQueue<>();
  private final long requestTimeWindow;
  private final int maxRequests;

  public Throttler(long requestTimeWindow, int maxRequests) {
    this.requestTimeWindow = requestTimeWindow;
    this.maxRequests = maxRequests;
  }

  @Override
  public ThrottleResult shouldProceed() {
    synchronized (requestTimes) {
      var currentTime = System.currentTimeMillis();
      Long discarded = null;
      var peek = requestTimes.peek();
      while (peek != null && (peek + requestTimeWindow < currentTime)) {
        discarded = requestTimes.poll();
        peek = requestTimes.peek();
      }
      if (discarded != null || (requestTimes.size() < maxRequests)) {
        requestTimes.offer(currentTime);
        return ThrottleResult.PROCEED;
      } else {
        return ThrottleResult.DO_NOT_PROCEED;
      }
    }
  }

  @Override
  public void notifyWhenCanProceed(Processor processor) {
    Executors.newSingleThreadExecutor().submit(() -> {
      while (shouldProceed() == ThrottleResult.DO_NOT_PROCEED) {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      processor.proceed();
    });
  }
}

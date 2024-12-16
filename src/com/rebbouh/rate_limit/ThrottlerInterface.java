package com.rebbouh.rate_limit;

public interface ThrottlerInterface {

  ThrottleResult shouldProceed();

  void notifyWhenCanProceed(Processor processor);

}
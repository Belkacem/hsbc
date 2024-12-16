package com.rebbouh.event_bus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CoalescingEventBusMultiThreadedTest {
  static class TestConsumer implements Consumer<Object> {
    List<Object> events = new ArrayList<>();

    @Override
    public void consume(Object event) throws Exception {
      events.add(event);
    }
  }

  public static void test() throws InterruptedException, ExecutionException {
    System.out.println(">>>>>>>>>>>>>>>>>>>>> Start Coalescing Multi Threaded Bus Tests <<<<<<<<<<<<<<<<<<<<<<<");
    var coalescingEventBusMultiThreaded = new CoalescingEventBusMultiThreaded();

    TestConsumer consumer = new TestConsumer();
    coalescingEventBusMultiThreaded.addSubscriberForFilteredEvents(consumer, null);
    TestConsumer consumer2 = new TestConsumer();
    coalescingEventBusMultiThreaded.addSubscriberForFilteredEvents(consumer, null);

    coalescingEventBusMultiThreaded.publishEvent("test 1");
    coalescingEventBusMultiThreaded.publishEvent("test 2");
    coalescingEventBusMultiThreaded.publishEvent("test 3");
    coalescingEventBusMultiThreaded.publishEvent(1);
    coalescingEventBusMultiThreaded.publishEvent(2);
    coalescingEventBusMultiThreaded.publishEvent(3);
    coalescingEventBusMultiThreaded.publishEvent(1d);
    coalescingEventBusMultiThreaded.publishEvent(2d);
    coalescingEventBusMultiThreaded.publishEvent(3d);
    Thread.sleep(2000);
    assert consumer.events.size() == 3;
    System.out.println(">>>>>>>>>>>>>>>>>>>>> Start Coalescing Multi Threaded Bus Tests <<<<<<<<<<<<<<<<<<<<<<<");
  }

}

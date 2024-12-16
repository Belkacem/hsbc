package com.rebbouh;

import com.rebbouh.event_bus.CoalescingEventBusMultiThreadedTest;
import com.rebbouh.event_bus.EventBusSingleThreadedTest;
import com.rebbouh.event_bus.FunEventBusSingleThreadedTest;
import com.rebbouh.rand.ProbabilisticRandomGenTests;

import java.util.concurrent.ExecutionException;

public class Main {

    // Tests are in the test package, these are just for development purposes.
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        // Execute
        ProbabilisticRandomGenTests.test();
        EventBusSingleThreadedTest.test();
        FunEventBusSingleThreadedTest.test();
        CoalescingEventBusMultiThreadedTest.test();
    }

}

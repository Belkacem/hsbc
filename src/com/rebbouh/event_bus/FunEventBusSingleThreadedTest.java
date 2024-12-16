package com.rebbouh.event_bus;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class FunEventBusSingleThreadedTest {

    public static void test() throws InterruptedException, ExecutionException {
        System.out.println(">>>>>>>>>>>>>>>>>>>>> Start Fun Single Threaded Bus Tests <<<<<<<<<<<<<<<<<<<<<<<");
        var results = new ArrayList<Object>();
        var stringEventBus = FunEventBusSingleThreaded.<String>getBus();
        stringEventBus.subscribe(results::add);
        stringEventBus.subscribe(results::add);
        stringEventBus.subscribe(results::add);
        // waiting for the bus to handle all the consumers.
        stringEventBus.publishEvent("test");
        assert results.size() == 3;
        results.clear();
        stringEventBus.subscribe(value -> {
            throw new Exception("This exception should not affect the other consumers");
        });
        stringEventBus.subscribe(results::add);
        stringEventBus.publishEvent("test 2");
        assert results.size() == 4;
        System.out.println(">>>>>>>>>>>>>>>>>>>>> End Fun Single Threaded Bus Tests <<<<<<<<<<<<<<<<<<<<<<<");
    }

}

package com.rebbouh.event_bus;

public class Publisher<T> {
    public final String name;
    public final EventBus eventBus;

    public Publisher(String name, EventBus eventBus) {
        this.name = name;
        this.eventBus = eventBus;
    }

    void publish(T event) throws Exception {
        if (event == null) {
            throw new NullPointerException("Can't publish null events.");
        }
        eventBus.publishEvent(event);
    }
}
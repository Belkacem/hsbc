package com.rebbouh.event_bus;

import java.util.function.Predicate;

public interface EventBus {

    void publishEvent(Object event);

    void addSubscriber(Class<?> clazz, Consumer<?> consumer);

    void addSubscriberForFilteredEvents(Consumer<?> consumer, Predicate<Object> filter);
}

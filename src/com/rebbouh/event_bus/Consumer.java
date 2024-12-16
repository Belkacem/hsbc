package com.rebbouh.event_bus;

public interface Consumer<T> {

    void consume(T event) throws Exception;
}
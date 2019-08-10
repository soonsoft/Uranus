package com.soonsoft.uranus.core.event;

import java.util.function.Consumer;

/**
 * IEvent
 */
public interface IEvent<E> {

    void on(Consumer<E> eventHandler);

    void off(Consumer<E> eventHandler);

    void trigger(E event);

    void clear();

}
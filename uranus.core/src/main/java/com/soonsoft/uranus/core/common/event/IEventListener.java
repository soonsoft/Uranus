package com.soonsoft.uranus.core.common.event;

import java.util.function.Consumer;

/**
 * IEvent
 */
public interface IEventListener<E> {

    void on(Consumer<E> eventHandler);

    void off(Consumer<E> eventHandler);

    void trigger(E event);

    void clear();

}
package com.soonsoft.uranus.core.common.event;

import java.util.function.Consumer;

/**
 * 事件处理器
 */
public interface IEventListener<E> {

    boolean on(Consumer<E> eventHandler);

    boolean off(Consumer<E> eventHandler);

    void trigger(E event);

    void clear();

    default String getName() {
        return null;
    }

}
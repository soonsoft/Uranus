package com.soonsoft.uranus.core.event;

import java.util.LinkedList;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SimpleEvent
 */
public class SimpleEvent<E> implements IEvent<E> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleEvent.class);

    private LinkedList<Consumer<E>> handlerList = new LinkedList<>();

    private String name;

    public SimpleEvent(String name) {
        this.name = name;
    }

    @Override
    public void on(Consumer<E> eventHandler) {
        if(eventHandler == null) {
            throw new IllegalArgumentException("the eventHandler is required.");
        }
        handlerList.add(eventHandler);
    }

    @Override
    public void off(Consumer<E> eventHandler) {
        if(eventHandler == null) {
            throw new IllegalArgumentException("the eventHandler is required.");
        }
        boolean result = handlerList.remove(eventHandler);
        if(!result) {
            LOGGER.warn("remove eventHandler from handlerList failed.");
        }
    }

    @Override
    public void trigger(E event) {
        for(Consumer<E> handler : handlerList) {
            handler.accept(event);
        }
    }

    @Override
    public void clear() {
        handlerList.clear();
    }

    public String getName() {
        return name;
    }

    
}
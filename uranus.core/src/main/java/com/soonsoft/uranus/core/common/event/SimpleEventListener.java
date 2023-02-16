package com.soonsoft.uranus.core.common.event;

import java.util.LinkedList;
import java.util.function.Consumer;

/**
 * 常规的事件处理器
 */
public class SimpleEventListener<E> implements IEventListener<E> {

    private LinkedList<Consumer<E>> handlerList = new LinkedList<>();

    private String name;

    public SimpleEventListener() {
        
    }

    public SimpleEventListener(String name) {
        this.name = name;
    }

    @Override
    public boolean on(Consumer<E> eventHandler) {
        if(eventHandler == null) {
            throw new IllegalArgumentException("the eventHandler is required.");
        }
        if(handlerList.contains(eventHandler)) {
            return false;
        }
        return handlerList.add(eventHandler);
    }

    @Override
    public boolean off(Consumer<E> eventHandler) {
        if(eventHandler == null) {
            throw new IllegalArgumentException("the eventHandler is required.");
        }
        return handlerList.remove(eventHandler);
    }

    @Override
    public void trigger(E event) {
        LinkedList<Consumer<E>> handlerList = getHandlerList();
        for(Consumer<E> handler : handlerList) {
            handler.accept(event);
        }
    }

    @Override
    public void clear() {
        handlerList.clear();
    }

    @Override
    public String getName() {
        return name;
    }

    protected LinkedList<Consumer<E>> getHandlerList() {
        return handlerList;
    }

    
}
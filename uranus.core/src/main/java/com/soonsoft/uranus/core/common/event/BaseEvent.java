package com.soonsoft.uranus.core.common.event;


public abstract class BaseEvent<T> implements IEvent<T> {

    private String name;
    private T data;

    public BaseEvent(String name, T data) {
        this.name = name;
        this.data = data;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public T getData() {
        return data;
    }

}
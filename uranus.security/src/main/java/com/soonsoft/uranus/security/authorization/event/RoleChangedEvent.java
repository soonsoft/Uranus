package com.soonsoft.uranus.security.authorization.event;

import java.util.function.Function;

import com.soonsoft.uranus.core.common.event.BaseEvent;


public class RoleChangedEvent<T> extends BaseEvent<T> {

    private Function<T, T> changedDataHandler;

    public RoleChangedEvent(T data, Function<T, T> changedDataHandler) {
        super("RoleChanged", data);
        this.changedDataHandler = changedDataHandler;
    }

    public T getChangedRole() {
        if(changedDataHandler != null) {
            return changedDataHandler.apply(getData());
        }
        return null;
    }

    
}
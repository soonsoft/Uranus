package com.soonsoft.uranus.security.authorization.event;

import java.util.function.Function;

import com.soonsoft.uranus.core.common.event.BaseEvent;


/**
 * RoleChangedEvent
 */
public class RoleChangedEvent<T> extends BaseEvent<T> {

    private Function<T, String> changedDataHandler;

    public RoleChangedEvent(T data, Function<T, String> changedDataHandler) {
        super("RoleChanged", data);
        this.changedDataHandler = changedDataHandler;
    }

    public String getChangedRole() {
        if(changedDataHandler != null) {
            return changedDataHandler.apply(getData());
        }
        return null;
    }

    
}
package com.soonsoft.uranus.security.authorization.event;

import com.soonsoft.uranus.core.common.event.BaseEvent;

public class FunctionChangedEvent<T> extends BaseEvent<T> {

    public FunctionChangedEvent(T data) {
        super("FunctionChanged", data);
    }
    
}
package com.soonsoft.uranus.core.common.attribute.access;

import com.soonsoft.uranus.core.functional.action.Action3;

public interface IAttributeAccessor<TValue> {
    
    TValue getValue();

    void setValue(TValue value);

    //void dependency(Action3<String, TValue, ActionType> action);

}

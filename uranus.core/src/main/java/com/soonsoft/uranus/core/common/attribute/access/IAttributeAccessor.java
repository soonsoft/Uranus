package com.soonsoft.uranus.core.common.attribute.access;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.common.attribute.Attribute;
import com.soonsoft.uranus.core.common.attribute.data.AttributeData;
import com.soonsoft.uranus.core.functional.action.Action3;
import com.soonsoft.uranus.core.functional.func.Func1;

public interface IAttributeAccessor {
    
    <TValue> TValue getValue(Attribute<TValue> attribute);

    <TValue> void setValue(TValue value, Attribute<TValue> attribute);

    <TValue> AttributeDataAccessor<TValue> getData(Attribute<TValue> attribute);

    void setData(AttributeData attributeData);

    //void dependency(Action3<String, TValue, ActionType> action);

}

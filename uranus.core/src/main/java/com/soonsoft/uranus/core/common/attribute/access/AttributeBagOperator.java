package com.soonsoft.uranus.core.common.attribute.access;

import com.soonsoft.uranus.core.common.attribute.data.AttributeData;
import com.soonsoft.uranus.core.functional.action.Action2;
import com.soonsoft.uranus.core.functional.action.Action3;
import com.soonsoft.uranus.core.functional.func.Func1;

class AttributeBagOperator {
    private Func1<Integer, AttributeData> attributeDataGetter;
    private Action2<Integer, AttributeData> attributeDataSetter;
    private Func1<AttributeData, Integer> attributeDataAdder;
    private Action3<ActionType, AttributeData, Object> notifyChanged;

    public void setAttributeDataGetter(Func1<Integer, AttributeData> attributeDataGetter) {
        this.attributeDataGetter = attributeDataGetter;
    }
    public AttributeData getAttributeData(Integer index) {
        return attributeDataGetter.call(index);
    } 

    public void setAttributeDataSetter(Action2<Integer, AttributeData> attributeDataSetter) {
        this.attributeDataSetter = attributeDataSetter;
    }
    public void setAttributeData(Integer index, AttributeData attributeData) {
        attributeDataSetter.apply(index, attributeData);
    }

    public void setAttributeDataAdder(Func1<AttributeData, Integer> attributeDataAdder) {
        this.attributeDataAdder = attributeDataAdder;
    }
    public Integer addAttributeData(AttributeData attributeData) {
        return attributeDataAdder.call(attributeData);
    }

    public void setNotifyChanged(Action3<ActionType, AttributeData, Object> notifyChanged) {
        this.notifyChanged = notifyChanged;
    }
    public Action3<ActionType, AttributeData, Object> getNotifyChanged() {
        return notifyChanged;
    }
    public void notifyChanged(ActionType type, AttributeData data) {
        notifyChanged(type, data, null);
    }
    public void notifyChanged(ActionType type, AttributeData data, Object oldValue) {
        notifyChanged.apply(type, data, oldValue);
    }
    
}

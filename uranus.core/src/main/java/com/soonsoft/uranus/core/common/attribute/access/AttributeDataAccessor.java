package com.soonsoft.uranus.core.common.attribute.access;

import com.soonsoft.uranus.core.common.attribute.Attribute;
import com.soonsoft.uranus.core.common.attribute.data.AttributeData;
import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.core.functional.action.Action3;

public class AttributeDataAccessor<TValue> {

    private final Attribute<TValue> attribute;
    private final AttributeData attributeData;
    private final Action3<ActionType, AttributeData, Object> notifyChanged;

    public AttributeDataAccessor(
            Attribute<TValue> attribute, 
            AttributeData attributeData, 
            Action3<ActionType, AttributeData, Object> notifyChanged) {
        this.attribute = attribute;
        this.attributeData = attributeData;
        this.notifyChanged = notifyChanged;
    }

    public TValue getValue() { 
        // 依赖收集
        TValue value = attribute.convertValue(attributeData.getPropertyValue());
        return value;
    }

    public void setValue(TValue value) {
        // changed notify
        String propertyValue = attribute.getConvertor().toStringValue(value);
        String oldPropertyValue = attributeData.getPropertyValue();
        if(StringUtils.equals(propertyValue, oldPropertyValue)) {
            return;
        }
        attributeData.setPropertyValue(propertyValue);
        notifyChanged.apply(ActionType.Modify, attributeData, attribute.getConvertor().convert(oldPropertyValue));
    }
    
}

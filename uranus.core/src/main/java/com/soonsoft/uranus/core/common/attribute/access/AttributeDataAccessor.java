package com.soonsoft.uranus.core.common.attribute.access;

import com.soonsoft.uranus.core.common.attribute.Attribute;
import com.soonsoft.uranus.core.common.attribute.data.AttributeData;
import com.soonsoft.uranus.core.common.attribute.notify.IAttributeValueChanged;

public class AttributeDataAccessor<TValue> {

    private final Attribute<TValue> attribute;
    private final AttributeData attributeData;
    private IAttributeValueChanged attributeValueChangedFn;

    public AttributeDataAccessor(Attribute<TValue> attribute, AttributeData attributeData) {
        this.attribute = attribute;
        this.attributeData = attributeData;
    }

    public TValue getValue() { 
        // 依赖收集
        TValue value = attribute.convertValue(attributeData.getPropertyValue());
        return value;
    }

    public void setValue(TValue value) {
        // changed notify
        String attributeValue = attribute.getConvertor().toAttributeValue(value);
        attributeData.setPropertyValue(attributeValue);
    }
    
}

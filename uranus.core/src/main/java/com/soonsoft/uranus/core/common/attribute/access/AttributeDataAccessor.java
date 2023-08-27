package com.soonsoft.uranus.core.common.attribute.access;

import com.soonsoft.uranus.core.common.attribute.Attribute;
import com.soonsoft.uranus.core.common.attribute.data.AttributeData;
import com.soonsoft.uranus.core.common.lang.StringUtils;

public class AttributeDataAccessor<TValue> {

    private final Attribute<TValue> attribute;
    private final AttributeData attributeData;
    protected final AttributeBagOperator attributeBagOperator;
    private final IndexNode node;

    public AttributeDataAccessor(
            IndexNode node,
            Attribute<TValue> attribute, 
            AttributeData attributeData, 
            AttributeBagOperator attributeBagOperator) {
        this.node = node;
        this.attribute = attribute;
        this.attributeData = attributeData;
        this.attributeBagOperator = attributeBagOperator;
    }

    public TValue getValue() { 
        // 依赖收集
        attributeBagOperator.collectDependency(attributeData.getKey());
        return attribute.convertValue(attributeData.getPropertyValue());
    }

    public void setValue(TValue value) {
        String propertyValue = attribute.getConvertor().toStringValue(value);
        String oldPropertyValue = attributeData.getPropertyValue();
        if(StringUtils.equals(propertyValue, oldPropertyValue)) {
            return;
        }
        attributeData.setPropertyValue(propertyValue);
        // 变更通知
        attributeBagOperator.notifyChanged(node, ActionType.Modify, attributeData, attribute.getConvertor().convert(oldPropertyValue));
    }
    
}

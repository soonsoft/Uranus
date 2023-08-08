package com.soonsoft.uranus.core.common.attribute.access;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.common.attribute.Attribute;
import com.soonsoft.uranus.core.common.attribute.access.IndexNode.ListNode;
import com.soonsoft.uranus.core.common.attribute.data.AttributeData;
import com.soonsoft.uranus.core.functional.func.Func1;

public class StructDataAccessor extends BaseAccessor {

    public StructDataAccessor(IndexNode node, Func1<Integer, AttributeData> attributeDataGetter, Func1<AttributeData, Integer> attributeDataAdder) {
        super(node, attributeDataGetter, attributeDataAdder);
    }

    public <TValue> TValue getValue(Attribute<TValue> attribute) {
        checkAttribute(attribute);
        AttributeData attributeData = getAttributeData(attribute.getPropertyName(), node, attributeDataGetter);
        return attributeData != null ? attribute.convertValue(attributeData.getPropertyValue()) : null;
    }

    public <TValue> void setValue(TValue value, Attribute<TValue> attribute) {
        checkAttribute(attribute);
        AttributeData attributeData = getAttributeData(attribute.getPropertyName(), node, attributeDataGetter);
        if(attributeData != null) {
            setAttributeData(attributeData, value, attribute, node);
        } else {
            addAttributeData(value, attribute, node, attributeDataGetter, attributeDataAdder);
        }
    }

    public <TValue> AttributeDataAccessor<TValue> getData(Attribute<TValue> attribute) {
        Guard.notNull(attribute, "the arguments [attribute] is required.");
        Guard.notEmpty(attribute.getPropertyName(), "the arguments [attribute.propertyName] is required.");
        IndexNode childNode = node.getChildNode(attribute.getPropertyName());
        AttributeData attributeData = attributeDataGetter.call(childNode.getIndex());

        return new AttributeDataAccessor<>(attribute, attributeData);
    }

    public StructDataAccessor getStruct(Attribute<?> attribute) {
        checkAttribute(attribute);
        IndexNode childNode = node.getChildNode(attribute.getPropertyName());
        return new StructDataAccessor(childNode, attributeDataGetter, attributeDataAdder);
    }

    public ArrayDataAccessor getArray(Attribute<?> attribute) {
        checkAttribute(attribute);
        IndexNode childNode = node.getChildNode(attribute.getPropertyName());
        if(childNode instanceof ListNode listNode) {
            return new ArrayDataAccessor(listNode, attributeDataGetter, attributeDataAdder);
        }
        ListNode listNode = new ListNode(childNode.getKey(), childNode.getParentKey(), childNode.getPropertyName());
        listNode.addChildNode(childNode);
        return new ArrayDataAccessor(listNode, attributeDataGetter, attributeDataAdder);
    }
    
}

package com.soonsoft.uranus.core.common.attribute.access;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.common.attribute.Attribute;
import com.soonsoft.uranus.core.common.attribute.access.IndexNode.ListNode;
import com.soonsoft.uranus.core.common.attribute.data.AttributeData;
import com.soonsoft.uranus.core.common.attribute.data.AttributeKey;

public class StructDataAccessor extends BaseAccessor {

    public StructDataAccessor(
            IndexNode node, 
            AttributeBagOperator attributeBagOperator,
            AttributeKey attributeKey) {
        super(node, attributeBagOperator, attributeKey);
    }

    public <TValue> TValue getValue(Attribute<TValue> attribute) {
        checkAttribute(attribute);
        AttributeData attributeData = getAttributeData(attribute.getPropertyName());
        return attributeData != null ? attribute.convertValue(attributeData.getPropertyValue()) : null;
    }

    public <TValue> void setValue(TValue value, Attribute<TValue> attribute) {
        checkAttribute(attribute);
        AttributeData attributeData = getAttributeData(attribute.getPropertyName());
        if(attributeData != null) {
            setAttributeData(attributeData, value, attribute);
        } else {
            addAttributeData(value, attribute);
        }
    }

    public <TValue> AttributeDataAccessor<TValue> getData(Attribute<TValue> attribute) {
        Guard.notNull(attribute, "the arguments [attribute] is required.");
        Guard.notEmpty(attribute.getPropertyName(), "the arguments [attribute.propertyName] is required.");
        IndexNode childNode = node.getChildNode(attribute.getPropertyName());
        AttributeData attributeData = attributeBagOperator.getAttributeData(childNode.getIndex());

        return attributeData != null ? new AttributeDataAccessor<>(attribute, attributeData, attributeBagOperator.getNotifyChanged()) : null;
    }

    public StructDataAccessor getStruct(Attribute<?> attribute) {
        checkAttribute(attribute);
        IndexNode childNode = node.getChildNode(attribute.getPropertyName());
        return new StructDataAccessor(childNode, attributeBagOperator, attributeKey);
    }

    public ArrayDataAccessor getArray(Attribute<?> attribute) {
        checkAttribute(attribute);
        IndexNode childNode = node.getChildNode(attribute.getPropertyName());
        if(childNode instanceof ListNode listNode) {
            return new ArrayDataAccessor(
                attribute.getEntityName(), 
                attribute.getPropertyName(), 
                listNode, 
                attributeBagOperator,
                attributeKey);
        }
        ListNode listNode = new ListNode(childNode.getKey(), childNode.getParentKey(), childNode.getPropertyName());
        listNode.addChildNode(childNode);
        return new ArrayDataAccessor(
            attribute.getEntityName(), 
            attribute.getPropertyName(), 
            listNode, 
            attributeBagOperator, 
            attributeKey);
    }
    
}

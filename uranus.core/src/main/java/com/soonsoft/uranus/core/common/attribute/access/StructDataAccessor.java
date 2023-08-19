package com.soonsoft.uranus.core.common.attribute.access;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.common.attribute.Attribute;
import com.soonsoft.uranus.core.common.attribute.access.IndexNode.ListNode;
import com.soonsoft.uranus.core.common.attribute.data.AttributeData;
import com.soonsoft.uranus.core.common.attribute.data.AttributeKey;
import com.soonsoft.uranus.core.functional.action.Action1;

public class StructDataAccessor extends BaseAccessor<StructDataAccessor> {

    public StructDataAccessor(
            IndexNode node, 
            AttributeBagOperator attributeBagOperator,
            AttributeKey attributeKey) {
        super(node, attributeBagOperator, attributeKey);
    }

    public <TValue> TValue getValue(Attribute<TValue> attribute) {
        checkAttribute(attribute);
        String propertyName = attribute.getPropertyName();
        if(node.contains(propertyName)) {
            AttributeData attributeData = getAttributeData(propertyName);
            attributeBagOperator.collectDependency(attributeData.getKey());
            return attributeData != null ? attribute.convertValue(attributeData.getPropertyValue()) : null;
        }
        return null;
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

        return attributeData != null ? new AttributeDataAccessor<>(attribute, attributeData, attributeBagOperator) : null;
    }

    public StructDataAccessor getStruct(Attribute<?> attribute) {
        checkAttribute(attribute);
        String propertyName = attribute.getPropertyName();
        if(node.contains(propertyName)) {
            IndexNode childNode = node.getChildNode(propertyName);
            return new StructDataAccessor(childNode, attributeBagOperator, attributeKey);
        }
        return null;
    }

    public ArrayDataAccessor getArray(Attribute<?> attribute) {
        checkAttribute(attribute);
        String propertyName = attribute.getPropertyName();
        if(!node.contains(propertyName)) {
            return null;
        }
        IndexNode childNode = node.getChildNode(propertyName);
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

    public void createComputedProperty(String propertyName, Action1<StructDataAccessor> computedAction) {
        // TODO 实现计算属性
    }
    
}

package com.soonsoft.uranus.core.common.attribute.access;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.common.attribute.Attribute;
import com.soonsoft.uranus.core.common.attribute.access.IndexNode.ListNode;
import com.soonsoft.uranus.core.common.attribute.data.AttributeData;
import com.soonsoft.uranus.core.common.attribute.data.AttributeKey;
import com.soonsoft.uranus.core.functional.action.Action2;
import com.soonsoft.uranus.core.functional.func.Func1;

public class StructDataAccessor extends BaseAccessor {

    public StructDataAccessor(
            IndexNode node, 
            Func1<Integer, AttributeData> attributeDataGetter, 
            Action2<Integer, AttributeData> attributeDataSetter,
            Func1<AttributeData, Integer> attributeDataAdder,
            Action2<ActionType, AttributeData> actionCommandPicker,
            AttributeKey attributeKey) {
        super(node, attributeDataGetter, attributeDataSetter, attributeDataAdder, actionCommandPicker, attributeKey);
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
        AttributeData attributeData = attributeDataGetter.call(childNode.getIndex());

        return attributeData != null ? new AttributeDataAccessor<>(attribute, attributeData) : null;
    }

    public StructDataAccessor getStruct(Attribute<?> attribute) {
        checkAttribute(attribute);
        IndexNode childNode = node.getChildNode(attribute.getPropertyName());
        return new StructDataAccessor(childNode, attributeDataGetter, attributeDataSetter, attributeDataAdder, actionCommandPicker, attributeKey);
    }

    public ArrayDataAccessor getArray(Attribute<?> attribute) {
        checkAttribute(attribute);
        IndexNode childNode = node.getChildNode(attribute.getPropertyName());
        if(childNode instanceof ListNode listNode) {
            return new ArrayDataAccessor(
                attribute.getEntityName(), 
                attribute.getPropertyName(), 
                listNode, 
                attributeDataGetter, 
                attributeDataSetter, 
                attributeDataAdder, 
                actionCommandPicker, 
                attributeKey);
        }
        ListNode listNode = new ListNode(childNode.getKey(), childNode.getParentKey(), childNode.getPropertyName());
        listNode.addChildNode(childNode);
        return new ArrayDataAccessor(
            attribute.getEntityName(), 
            attribute.getPropertyName(), 
            listNode, 
            attributeDataGetter, 
            attributeDataSetter, 
            attributeDataAdder, 
            actionCommandPicker, 
            attributeKey);
    }
    
}

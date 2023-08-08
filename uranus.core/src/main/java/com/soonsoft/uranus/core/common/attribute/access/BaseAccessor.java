package com.soonsoft.uranus.core.common.attribute.access;

import java.util.UUID;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.common.attribute.Attribute;
import com.soonsoft.uranus.core.common.attribute.access.IndexNode.EntityNode;
import com.soonsoft.uranus.core.common.attribute.data.AttributeData;
import com.soonsoft.uranus.core.functional.func.Func1;

public abstract class BaseAccessor {
    protected final IndexNode node;
    protected final Func1<Integer, AttributeData> attributeDataGetter;
    protected final Func1<AttributeData, Integer> attributeDataAdder;

    public BaseAccessor(IndexNode node, Func1<Integer, AttributeData> attributeDataGetter, Func1<AttributeData, Integer> attributeDataAdder) {
        this.node = node;
        this.attributeDataGetter = attributeDataGetter;
        this.attributeDataAdder = attributeDataAdder;
    }

    protected String createID() {
        return UUID.randomUUID().toString();
    }

    protected AttributeData getAttributeData(String propertyName, IndexNode node, Func1<Integer, AttributeData> attributeDataGetter) {
        IndexNode childNode = node.getChildNode(propertyName);
        AttributeData attributeData = attributeDataGetter.call(childNode.getIndex());
        return attributeData;
    }

    protected <TValue> void addAttributeData(TValue value, Attribute<TValue> attribute, IndexNode node, 
            Func1<Integer, AttributeData> attributeDataGetter, Func1<AttributeData, Integer> attributeDataAdder) {
        String strValue = attribute.getConvertor().toAttributeValue(value);
        AttributeData parentAttributeData = 
            node.getIndex() > -1 
                ? attributeDataGetter.call(node.getIndex())
                : ((EntityNode) node).getVirtualAttributeData();

        AttributeData attributeData = new AttributeData();
        attributeData.setId(createID());
        attributeData.setParentId(parentAttributeData.getId());
        attributeData.setDataId(parentAttributeData.getDataId());
        attributeData.setEntityName(parentAttributeData.getEntityName());
        attributeData.setPropertyName(attribute.getPropertyName());
        attributeData.setPropertyValue(strValue);
        
        Integer index = attributeDataAdder.call(attributeData);
        IndexNode newNode = new IndexNode(attributeData.getId(), node.getKey(), attribute.getPropertyName(), index.intValue());
        node.addChildNode(newNode);
    }

    protected <TValue> void setAttributeData(AttributeData attributeData, TValue value, Attribute<TValue> attribute, IndexNode node) {
        String strValue = attribute.getConvertor().toAttributeValue(value);
        TValue oldValue = attribute.convertValue(attributeData.getPropertyValue());
        attributeData.setPropertyValue(strValue);

        // notify(oldValue);
    }

    protected void checkAttribute(Attribute<?> attribute) {
        Guard.notNull(attribute, "the arguments [attribute] is required.");
        Guard.notEmpty(attribute.getPropertyName(), "the arguments [attribute.propertyName] is required.");
    }

    //void dependency(Action3<String, TValue, ActionType> action);

}

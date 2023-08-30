package com.soonsoft.uranus.core.common.attribute.access;

import com.soonsoft.uranus.core.common.attribute.Attribute;
import com.soonsoft.uranus.core.common.attribute.access.IndexNode.ListNode;
import com.soonsoft.uranus.core.common.attribute.data.AttributeData;
import com.soonsoft.uranus.core.common.attribute.data.AttributeKey;
import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.core.error.argument.ArgumentException;

public class ArrayDataAccessor extends BaseAccessor<ArrayDataAccessor> {
    private final String entityName;
    private final String propertyName;

    public ArrayDataAccessor(
            String entityName, String propertyName,
            ListNode node, 
            AttributeBagOperator attributeBagOperator,
            AttributeKey attributeKey) {
        super(node, attributeBagOperator, attributeKey);
        this.entityName = entityName;
        this.propertyName = propertyName;
    }

    public <TValue> TValue getValue(int index, Attribute<TValue> attribute) {
        checkIndex(index);
        checkAttribute(attribute);

        AttributeData attributeData = getAttributeData(String.valueOf(index));
        attributeBagOperator.collectDependency(attributeData.getKey());
        return attributeData != null ? attribute.convertValue(attributeData.getPropertyValue()) : null;
    }

    public <TValue> void setValue(TValue value, int index, Attribute<TValue> attribute) {
        checkIndex(index);
        checkAttribute(attribute);

        AttributeData attributeData = getAttributeData(String.valueOf(index));
        setAttributeData(attributeData, value, attribute);
    }

    public <TValue> void addValue(TValue value, Attribute<TValue> attribute) {
        checkAttribute(attribute);
        addAttributeData(value, attribute);
    }

    public <TValue> StructDataAccessor addStruct(Attribute<TValue> attribute) {
        checkAttribute(attribute);
        return newStruct(attribute.getEntityName(), attribute.getPropertyName());
    }

    public <TValue> AttributeDataAccessor<TValue> getData(int index, Attribute<TValue> attribute) {
        checkIndex(index);
        checkAttribute(attribute);

        AttributeData attributeData = getAttributeData(String.valueOf(index));
        return attributeData != null ? new AttributeDataAccessor<>(node, attribute, attributeData, attributeBagOperator) : null;
    }

    public StructDataAccessor getStruct(int index, Attribute<?> attribute) {
        checkIndex(index);
        checkAttribute(attribute);

        IndexNode childNode = node.getChildNode(String.valueOf(index));
        StructDataAccessor structAccessor = createStructDataAccessor(childNode);

        // 依赖收集
        attributeBagOperator.collectDependency(childNode.getDependencyKey());

        return structAccessor;
    }

    public ArrayDataAccessor getArray(int index, Attribute<?> attribute) {
        checkIndex(index);
        checkAttribute(attribute);

        ArrayDataAccessor arrayAccessor = null;
        ListNode listNode = null;

        IndexNode childNode = node.getChildNode(String.valueOf(index));
        if(childNode instanceof ListNode) {
            listNode = (ListNode) childNode;
            arrayAccessor = createArrayDataAccessor(attribute.getEntityName(), attribute.getPropertyName(), listNode);
        } else {
            listNode = new ListNode(childNode.getKey(), childNode.getParentKey(), childNode.getPropertyName());
            listNode.addChildNode(childNode);
            arrayAccessor = createArrayDataAccessor(attribute.getEntityName(), attribute.getPropertyName(), listNode);
        }

        // 依赖收集
        attributeBagOperator.collectDependency(listNode.getDependencyKey());

        return arrayAccessor;
    }

    public int size() {
        return node.getChildren() != null ? node.getChildren().size() : 0;
    }

    @Override
    protected void checkAttribute(Attribute<?> attribute) {
        super.checkAttribute(attribute);
        if(!StringUtils.equals(entityName, attribute.getEntityName())) {
            throw new ArgumentException(StringUtils.format("the attribute.entityName [{0}] is not [{1}]", attribute.getEntityName(), entityName));
        }
        if(!StringUtils.equals(propertyName, attribute.getPropertyName())) {
            throw new ArgumentException(StringUtils.format("the attribute.propertyName [{0}] is not [{1}]", attribute.getPropertyName(), propertyName));
        }
    }

    private void checkIndex(int index) {
        if(index < 0 || index >= node.getChildren().size()) {
            throw new IndexOutOfBoundsException(index);
        }
    }
    
}

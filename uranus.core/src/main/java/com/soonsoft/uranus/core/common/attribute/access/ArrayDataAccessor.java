package com.soonsoft.uranus.core.common.attribute.access;

import com.soonsoft.uranus.core.common.attribute.Attribute;
import com.soonsoft.uranus.core.common.attribute.access.IndexNode.ListNode;
import com.soonsoft.uranus.core.common.attribute.data.AttributeData;
import com.soonsoft.uranus.core.common.attribute.data.AttributeKey;
import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.core.error.argument.ArgumentException;
import com.soonsoft.uranus.core.functional.action.Action2;
import com.soonsoft.uranus.core.functional.action.Action3;
import com.soonsoft.uranus.core.functional.behavior.ForEachBehavior;
import com.soonsoft.uranus.core.functional.behavior.IForEach;
import com.soonsoft.uranus.core.functional.func.Func1;

public class ArrayDataAccessor extends BaseAccessor implements IForEach<AttributeData> {
    private final String entityName;
    private final String propertyName;

    public ArrayDataAccessor(
            String entityName, String propertyName,
            ListNode node, 
            Func1<Integer, AttributeData> attributeDataGetter, 
            Action2<Integer, AttributeData> attributeDataSetter,
            Func1<AttributeData, Integer> attributeDataAdder,
            Action3<ActionType, AttributeData, Object> notifyChanged,
            AttributeKey attributeKey) {
        super(node, attributeDataGetter, attributeDataSetter, attributeDataAdder, notifyChanged, attributeKey);
        this.entityName = entityName;
        this.propertyName = propertyName;
    }

    public <TValue> TValue getValue(int index, Attribute<TValue> attribute) {
        checkIndex(index);
        checkAttribute(attribute);

        AttributeData attributeData = getAttributeData(String.valueOf(index));
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

    public <TValue> AttributeDataAccessor<TValue> getData(int index, Attribute<TValue> attribute) {
        checkIndex(index);
        checkAttribute(attribute);

        AttributeData attributeData = getAttributeData(String.valueOf(index));
        return attributeData != null ? new AttributeDataAccessor<>(attribute, attributeData) : null;
    }

    public StructDataAccessor getStruct(int index, Attribute<?> attribute) {
        checkIndex(index);
        checkAttribute(attribute);
        IndexNode childNode = node.getChildNode(String.valueOf(index));
        return createStructDataAccessor(childNode);
    }

    public ArrayDataAccessor getArray(int index, Attribute<?> attribute) {
        checkIndex(index);
        checkAttribute(attribute);
        IndexNode childNode = node.getChildNode(String.valueOf(index));
        if(childNode instanceof ListNode listNode) {
            return createArrayDataAccessor(attribute.getEntityName(), attribute.getPropertyName(), listNode);
        }
        ListNode listNode = new ListNode(childNode.getKey(), childNode.getParentKey(), childNode.getPropertyName());
        listNode.addChildNode(childNode);
        return createArrayDataAccessor(attribute.getEntityName(), attribute.getPropertyName(), listNode);
    }

    public int size() {
        return node.getChildren() != null ? node.getChildren().size() : 0;
    }

    @Override
    public void forEach(Action3<AttributeData, Integer, ForEachBehavior> action) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'forEach'");
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

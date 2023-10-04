package com.soonsoft.uranus.core.common.attribute.access;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.common.attribute.Attribute;
import com.soonsoft.uranus.core.common.attribute.AttributeException;
import com.soonsoft.uranus.core.common.attribute.access.IndexNode.ListNode;
import com.soonsoft.uranus.core.common.attribute.data.AttributeData;
import com.soonsoft.uranus.core.common.attribute.data.AttributeKey;
import com.soonsoft.uranus.core.common.attribute.data.PropertyType;
import com.soonsoft.uranus.core.common.attribute.notify.ComputedWatcher;
import com.soonsoft.uranus.core.functional.action.Action1;
import com.soonsoft.uranus.core.functional.func.Func1;

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

        return attributeData != null ? new AttributeDataAccessor<>(node, attribute, attributeData, attributeBagOperator) : null;
    }

    public StructDataAccessor getStruct(Attribute<?> attribute) {
        checkAttribute(attribute);
        String propertyName = attribute.getPropertyName();
        if(!node.contains(propertyName)) {
            return null;
        }

        IndexNode childNode = node.getChildNode(propertyName);
        StructDataAccessor structAccessor = createStructDataAccessor(childNode);

         // 依赖收集
         attributeBagOperator.collectDependency(childNode.getDependencyKey());

        return structAccessor;
    }

    public ArrayDataAccessor getArray(Attribute<?> attribute) {
        checkAttribute(attribute);
        String propertyName = attribute.getPropertyName();
        if(!node.contains(propertyName)) {
            return null;
        }
        
        ArrayDataAccessor arrayAccessor = null;
        ListNode listNode = null;
        IndexNode childNode = node.getChildNode(propertyName);
        if(childNode instanceof ListNode) {
            listNode = (ListNode) childNode;
            arrayAccessor = createArrayDataAccessor(attribute.getEntityName(), attribute.getPropertyName(), listNode);
        } else {
            // 把原本只有一个的属性，以数组的方式返回
            listNode = new ListNode(childNode.getKey(), childNode.getParentKey(), childNode.getPropertyName());
            listNode.addChildNode(childNode);

            arrayAccessor = createArrayDataAccessor(attribute.getEntityName(), attribute.getPropertyName(), listNode);
        }

        // 依赖收集
        attributeBagOperator.collectDependency(listNode.getDependencyKey());

        return arrayAccessor;
        
    }

    public <TValue> void createComputedProperty(Attribute<TValue> attribute, Func1<StructDataAccessor, TValue> computedFn) {
        checkAttribute(attribute);
        checkPropertyName(attribute.getPropertyName());
        if(attribute.getType() != PropertyType.ComputedProperty) {
            throw new AttributeException("unexpected propertyType [%s]", attribute.getType().name());
        }
        if(computedFn == null) {
            Guard.notNull(computedFn, "the arguments computedFn is required.");
        }

        AttributeData computedAttributeData = createAttributeData(attribute.getEntityName(), attribute.getPropertyName(), null);
        computedAttributeData.setPropertyType(PropertyType.ComputedProperty);

        ComputedWatcher<StructDataAccessor, TValue> watcher = new ComputedWatcher<>(this, attributeBagOperator.getDependency(), computedFn);
        Action1<TValue> updateAction = value -> {
            TValue oldValue = attribute.getConvertor().convert(computedAttributeData.getPropertyValue());
            computedAttributeData.setPropertyValue(attribute.getConvertor().toStringValue(value));
            attributeBagOperator.notifyChanged(node, ActionType.Modify, computedAttributeData, oldValue);
        };
        watcher.setUpdateAction(updateAction);
        computedAttributeData.setPropertyValue(attribute.getConvertor().toStringValue(watcher.getComputedValue()));

        Integer index = attributeBagOperator.addAttributeData(computedAttributeData);
        IndexNode indexNode = new IndexNode(computedAttributeData.getKey(), computedAttributeData.getParentKey(), attribute.getPropertyName(), index.intValue());
        node.addChildNode(indexNode);

        attributeBagOperator.notifyChanged(node, ActionType.Add, computedAttributeData);
    }
    
}

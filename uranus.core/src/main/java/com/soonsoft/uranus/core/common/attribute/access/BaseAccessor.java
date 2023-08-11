package com.soonsoft.uranus.core.common.attribute.access;

import java.util.Map;
import java.util.Stack;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.common.attribute.Attribute;
import com.soonsoft.uranus.core.common.attribute.AttributeException;
import com.soonsoft.uranus.core.common.attribute.access.IndexNode.EntityNode;
import com.soonsoft.uranus.core.common.attribute.access.IndexNode.ListNode;
import com.soonsoft.uranus.core.common.attribute.data.AttributeData;
import com.soonsoft.uranus.core.common.attribute.data.AttributeKey;
import com.soonsoft.uranus.core.common.attribute.data.DataStatus;
import com.soonsoft.uranus.core.functional.action.Action1;
import com.soonsoft.uranus.core.functional.action.Action2;
import com.soonsoft.uranus.core.functional.func.Func1;

public abstract class BaseAccessor {
    protected final IndexNode node;
    protected final Func1<Integer, AttributeData> attributeDataGetter;
    protected final Action2<Integer, AttributeData> attributeDataSetter;
    protected final Func1<AttributeData, Integer> attributeDataAdder;
    protected final Action2<ActionType, AttributeData> actionCommandPicker;
    protected final AttributeKey attributeKey;

    public BaseAccessor(
            IndexNode node, 
            Func1<Integer, AttributeData> attributeDataGetter, 
            Action2<Integer, AttributeData> attributeDataSetter,
            Func1<AttributeData, Integer> attributeDataAdder, 
            Action2<ActionType, AttributeData> actionCommandPicker,
            AttributeKey attributeKey) {
        this.node = node;
        this.attributeDataGetter = attributeDataGetter;
        this.attributeDataSetter = attributeDataSetter;
        this.attributeDataAdder = attributeDataAdder;
        this.actionCommandPicker = actionCommandPicker;
        this.attributeKey = attributeKey;
    }

    public boolean delete(String propertyName) {
        if(!node.contains(propertyName)) {
            return false;
        }
        IndexNode deleteRoot = node.getChildNode(propertyName);
        node.removeChildNode(propertyName);
        deepForeach(deleteRoot, itemNode -> {
            if(itemNode instanceof ListNode) {
                return;
            }
            AttributeData attributeData = attributeDataGetter.call(itemNode.getIndex());
            attributeDataSetter.apply(itemNode.getIndex(), null);
            if(attributeData.getStatus() != DataStatus.Temp) {
                actionCommandPicker.apply(ActionType.Delete, attributeData);
            }
        });
        return true;
    }

    public StructDataAccessor addStruct(String entityName, String propertyName) {
        if(node.contains(propertyName)) {
            throw new AttributeException("the arguments propertyName[%s] is exists.", propertyName);
        }
        AttributeData attributeData = createAttributeData(entityName, propertyName, null);
        attributeData.setStatus(DataStatus.Temp);
        Integer index = attributeDataAdder.call(attributeData);
        IndexNode structNode = new IndexNode(attributeData.getKey(), attributeData.getParentKey(), propertyName, index.intValue());

        return new StructDataAccessor(structNode, attributeDataGetter, attributeDataAdder, attributeKey);
    }

    public ArrayDataAccessor addArray(String entityName, String propertyName) {
        if(node.contains(propertyName)) {
            throw new AttributeException("the arguments propertyName[%s] is exists.", propertyName);
        }
        String key = attributeKey.generate();
        String parentKey = node.getKey();
        ListNode listNode = new ListNode(key, parentKey, propertyName);

        return createArrayDataAccessor(entityName, propertyName, listNode);
    }

    protected AttributeData getAttributeData(String propertyName) {
        IndexNode childNode = node.getChildNode(propertyName);
        AttributeData attributeData = attributeDataGetter.call(childNode.getIndex());
        return attributeData;
    }

    protected <TValue> void addAttributeData(TValue value, Attribute<TValue> attribute) {
        String strValue = attribute.getConvertor().toAttributeValue(value);
        AttributeData attributeData = createAttributeData(null, attribute.getPropertyName(), strValue);
        
        Integer index = attributeDataAdder.call(attributeData);
        IndexNode newNode = new IndexNode(attributeData.getKey(), node.getKey(), attribute.getPropertyName(), index.intValue());
        node.addChildNode(newNode);
    }

    protected <TValue> void setAttributeData(AttributeData attributeData, TValue value, Attribute<TValue> attribute) {
        String strValue = attribute.getConvertor().toAttributeValue(value);
        TValue oldValue = attribute.convertValue(attributeData.getPropertyValue());
        attributeData.setPropertyValue(strValue);

        // notify(oldValue);
    }

    protected StructDataAccessor createStructDataAccessor(IndexNode structNode) {
        
    }

    protected ArrayDataAccessor createArrayDataAccessor(String entityName, String propertyName, ListNode listNode) {
        return new ArrayDataAccessor(
                entityName, 
                propertyName, 
                listNode, 
                attributeDataGetter, 
                attributeDataSetter, 
                attributeDataAdder, 
                actionCommandPicker, 
                attributeKey); 
    }

    protected void deepForeach(IndexNode root, Action1<IndexNode> fn) {
        if(root == null) {
            return;
        }

        Stack<IndexNode> stack = new Stack<>();
        stack.push(root);

        while(!stack.isEmpty()) {
            IndexNode node = stack.pop();
            fn.apply(node);

            if(node.getChildren() != null && !node.getChildren().isEmpty()) {
                for(Map.Entry<String, IndexNode> entry : node.getChildren().entrySet()) {
                    fn.apply(entry.getValue());
                }
            }
        }
    }

    protected void checkAttribute(Attribute<?> attribute) {
        Guard.notNull(attribute, "the arguments [attribute] is required.");
        Guard.notEmpty(attribute.getEntityName(), "the arguments [attribute.entityName] is required.");
        Guard.notEmpty(attribute.getPropertyName(), "the arguments [attribute.propertyName] is required.");
    }

    private AttributeData createAttributeData(String entityName, String propertyName, String strValue) {
        AttributeData parentAttributeData = 
            node.getIndex() > -1 
                ? attributeDataGetter.call(node.getIndex())
                : ((EntityNode) node).getVirtualAttributeData();

        AttributeData attributeData = new AttributeData();
        attributeData.setKey(attributeKey.generate());
        attributeData.setParentKey(parentAttributeData.getKey());
        attributeData.setDataId(parentAttributeData.getDataId());
        attributeData.setEntityName(entityName);
        attributeData.setPropertyName(propertyName);
        attributeData.setPropertyValue(strValue);
        return attributeData;
    }

    //void dependency(Action3<String, TValue, ActionType> action);

}

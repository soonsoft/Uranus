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
import com.soonsoft.uranus.core.common.attribute.data.PropertyType;
import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.core.functional.action.Action1;
import com.soonsoft.uranus.core.functional.action.Action4;

public abstract class BaseAccessor<TAccessor> {
    protected final IndexNode node;
    protected final AttributeBagOperator attributeBagOperator;
    protected final AttributeKey attributeKey;

    public BaseAccessor(
            IndexNode node, 
            AttributeBagOperator attributeBagOperator,
            AttributeKey attributeKey) {
        this.node = node;
        this.attributeBagOperator = attributeBagOperator;
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
            AttributeData attributeData = attributeBagOperator.getAttributeData(itemNode.getIndex());
            attributeBagOperator.setAttributeData(itemNode.getIndex(), null);
            attributeBagOperator.notifyChanged(ActionType.Delete, attributeData, null);
        });
        return true;
    }

    /**
     * 创建对象属性
     * @param entityName 实体名称
     * @param propertyName 属性名称
     * @return 返回 StructDataAccessor 实例
     */
    public StructDataAccessor newStruct(String entityName, String propertyName) {
        checkPropertyName(propertyName);

        AttributeData attributeData = createAttributeData(entityName, propertyName, null);
        attributeData.setPropertyType(PropertyType.Struct);
        attributeData.setStatus(DataStatus.Temp);
        Integer index = attributeBagOperator.addAttributeData(attributeData);
        IndexNode structNode = new IndexNode(attributeData.getKey(), attributeData.getParentKey(), propertyName, index.intValue());
        node.addChildNode(structNode);

        return createStructDataAccessor(structNode);
    }

    /**
     * 创建数组属性
     * @param propertyName 属性名称
     * @return 返回 ArrayDataAccessor 实例
     */
    public ArrayDataAccessor newArray(String propertyName) {
        checkPropertyName(propertyName);

        AttributeData attributeData = null;
        if(node instanceof EntityNode entityNode) {
            attributeData = entityNode.getVirtualAttributeData();
        } else {
            attributeData = attributeBagOperator.getAttributeData(node.getIndex());
        }
        String entityName = attributeData.getEntityName();
        
        String key = attributeKey.generate();
        String parentKey = node.getKey();
        ListNode listNode = new ListNode(key, parentKey, propertyName).init(entityName, attributeData.getDataId());
        node.addChildNode(listNode);

        return createArrayDataAccessor(entityName, propertyName, listNode);
    }

    public void watch(String propertyName, Action4<TAccessor, String, Object, Object> action) {
        // TODO 实现监听器
        if(!node.contains(propertyName)) {
            throw new AttributeException("the propertyName [%s] is not exists.",  propertyName);
        }

        AttributeData attributeData = getAttributeData(propertyName);
        //attributeData.getKey();
    }

    protected AttributeData getAttributeData(String propertyName) {
        IndexNode childNode = node.getChildNode(propertyName);
        if(childNode == null) {
            return null;
        }
        AttributeData attributeData = attributeBagOperator.getAttributeData(childNode.getIndex());
        return attributeData;
    }

    protected <TValue> void addAttributeData(TValue value, Attribute<TValue> attribute) {
        String strValue = attribute.getConvertor().toStringValue(value);
        AttributeData attributeData = createAttributeData(null, attribute.getPropertyName(), strValue);
        attributeData.setStatus(DataStatus.Temp);
        
        Integer index = attributeBagOperator.addAttributeData(attributeData);
        IndexNode newNode = new IndexNode(attributeData.getKey(), node.getKey(), attribute.getPropertyName(), index.intValue());
        node.addChildNode(newNode);

        attributeBagOperator.notifyChanged(ActionType.Add, attributeData, null);
    }

    protected <TValue> void setAttributeData(AttributeData attributeData, TValue value, Attribute<TValue> attribute) {
        String strValue = attribute.getConvertor().toStringValue(value);
        if(StringUtils.equals(strValue, attributeData.getPropertyValue())) {
            return;
        }
        TValue oldValue = attribute.convertValue(attributeData.getPropertyValue());
        attributeData.setPropertyValue(strValue);

        attributeBagOperator.notifyChanged(ActionType.Modify, attributeData, oldValue);
    }

    protected StructDataAccessor createStructDataAccessor(IndexNode structNode) {
        return new StructDataAccessor(
                structNode, 
                attributeBagOperator, 
                attributeKey);
    }

    protected ArrayDataAccessor createArrayDataAccessor(String entityName, String propertyName, ListNode listNode) {
        return new ArrayDataAccessor(
                entityName, 
                propertyName, 
                listNode, 
                attributeBagOperator, 
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

    protected AttributeData createAttributeData(String entityName, String propertyName, String strValue) {
        AttributeData parentAttributeData = 
            node.getIndex() > -1
                ? attributeBagOperator.getAttributeData(node.getIndex())
                : (node instanceof EntityNode e
                    ? e.getVirtualAttributeData()
                    : ((ListNode) node).getVirtualAttributeData());

        AttributeData attributeData = new AttributeData();
        attributeData.setKey(attributeKey.generate());
        attributeData.setParentKey(parentAttributeData.getKey());
        attributeData.setDataId(parentAttributeData.getDataId());
        attributeData.setEntityName(entityName);
        attributeData.setPropertyName(propertyName);
        attributeData.setPropertyValue(strValue);
        return attributeData;
    }

    protected void checkAttribute(Attribute<?> attribute) {
        Guard.notNull(attribute, "the arguments [attribute] is required.");
        Guard.notEmpty(attribute.getEntityName(), "the arguments [attribute.entityName] is required.");
        Guard.notEmpty(attribute.getPropertyName(), "the arguments [attribute.propertyName] is required.");
    }

    protected void checkPropertyName(String propertyName) {
        Guard.notEmpty(propertyName, "the arguments propertyName is required.");
        if(node.contains(propertyName)) {
            throw new AttributeException("the propertyName [%s] is exists.", propertyName);
        }
    }

}

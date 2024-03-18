package com.soonsoft.uranus.core.common.attribute.access;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.common.attribute.ComputedAttribute;
import com.soonsoft.uranus.core.common.attribute.IAttributeBag;
import com.soonsoft.uranus.core.common.attribute.access.IndexNode.*;
import com.soonsoft.uranus.core.common.attribute.data.AttributeData;
import com.soonsoft.uranus.core.common.attribute.data.AttributeKey;
import com.soonsoft.uranus.core.common.attribute.data.DataStatus;
import com.soonsoft.uranus.core.common.attribute.data.PropertyType;
import com.soonsoft.uranus.core.common.attribute.notify.ComputedWatcher;
import com.soonsoft.uranus.core.common.attribute.notify.Dependency;
import com.soonsoft.uranus.core.common.collection.MapUtils;
import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.core.functional.action.Action1;
import com.soonsoft.uranus.core.functional.func.Func1;

public class AttributeBag implements IAttributeBag {

    private final List<AttributeData> attributeDataList;
    private final AttributeBagOperator attributeBagOperator;
    private Map<String, IndexNode> indexes = null;
    private AttributeKey attributeKey = new AttributeKey();
    private ActionCommandPackage actionCommandPackage = new ActionCommandPackage();
    private Dependency<String> dependency;
    private final static String ROOT_KEY = "__ROOT__";

    public AttributeBag(Dependency<String> dependency) {
        this(new ArrayList<>(), dependency, null);
    }

    public AttributeBag(List<AttributeData> attributeDataList, Dependency<String> dependency) {
        this(attributeDataList, dependency, null);
    }

    public AttributeBag(List<AttributeData> attributeDataList, Dependency<String> dependency, Func1<AttributeData, ComputedAttribute<?>> computedAttributeFinder) {
        Guard.notNull(attributeDataList, "the arguments attributeDataList is required.");
        Guard.notNull(dependency, "the arguments dependency is required.");

        this.attributeDataList = attributeDataList;
        this.dependency = dependency;

        this.attributeBagOperator = initOperator();
        this.indexes = initData(computedAttributeFinder);
    }

    protected AttributeBagOperator initOperator() {
        AttributeBagOperator operator = new AttributeBagOperator();
        operator.setAttributeDataGetter(index -> attributeDataList.get(index.intValue()));
        operator.setAttributeDataSetter((index, attrData) -> attributeDataList.set(index.intValue(), attrData));
        operator.setAttributeDataAdder(item -> {
            int index = attributeDataList.size();
            if(attributeDataList.add(item)) {
                return index;
            }
            return -1;
        });
        operator.setNotifyChangedFn((node, type, data, oldValue) -> onNotifyChanged(node, type, data, oldValue));
        operator.setCollectDependencyFn(key -> onDepend(key));
        operator.setDependencyGetter(() -> dependency);
        return operator;
    }

    protected Map<String, IndexNode> initData(Func1<AttributeData, ComputedAttribute<?>> computedAttributeFinder) {
        if(attributeDataList == null || attributeDataList.isEmpty()) {
            return new LinkedHashMap<>();
        }

        Map<String, IndexNode> map = MapUtils.createHashMap(attributeDataList.size() + 10);
        RootNode rootNode = new RootNode(ROOT_KEY);

        List<IndexNode> computedPropertyNodeList = new ArrayList<>();
        int index = 0;
        for(AttributeData attributeData : attributeDataList) {
            String entityName = attributeData.getRootEntityName();
            String key = attributeData.getKey();
            String parentKey = attributeData.getParentKey();
            String propertyName = attributeData.getPropertyName();

            IndexNode parentNode;
            if(StringUtils.isEmpty(parentKey)) {
                IndexNode entityNode = map.get(entityName);
                if(entityNode == null) {
                    entityNode = 
                        new EntityNode(entityName, ROOT_KEY)
                            .init(entityName, attributeData.getDataId());
                    map.put(entityName, entityNode);
                    rootNode.addChildNode(entityNode);
                }
                parentNode = entityNode;
                parentKey = entityName;
            } else {
                parentNode = map.get(parentKey);
                if(parentNode == null) {
                    parentNode = new TempIndexNode(key, parentKey);
                }
            }

            IndexNode node = map.get(key);
            if(node instanceof TempIndexNode tempNode) {
                node = new IndexNode(tempNode.getKey(), tempNode.getParentKey(), propertyName, index, tempNode.getChildren());
                map.put(key, node);
            } else {
                node = node == null ? parentNode.getChildNode(propertyName) : null;
                if(node == null) {
                    node = new IndexNode(key, parentKey, propertyName, index);
                    map.put(key, node);
                } else if(node instanceof ListNode listNode) {
                    // 是数组，将当前元素加入数组
                    IndexNode newNode = new IndexNode(key, parentKey, propertyName, index);
                    listNode.addChildNode(newNode);
                } else {
                    // 同名属性，合并为数组
                    ListNode listNode = new ListNode(attributeKey.generate(), parentKey, propertyName).init(entityName, attributeData.getDataId());
                    listNode.addChildNode(node);
                    IndexNode newNode = new IndexNode(key, parentKey, propertyName, index);
                    listNode.addChildNode(newNode);
                    map.put(key, listNode);
                    // 用于更新 parentNode chlidren
                    node = listNode;
                }
            }

            if(attributeData.getPropertyType() == PropertyType.ComputedProperty) {
                computedPropertyNodeList.add(node);
            }

            parentNode.addChildNode(node);

            index++;
        }

        if(computedAttributeFinder != null) {
            for(IndexNode propertyNode : computedPropertyNodeList) {
                AttributeData computedAttributeData = attributeDataList.get(propertyNode.getIndex());
                ComputedAttribute<?> computedAttribute = computedAttributeFinder.call(computedAttributeData);
                resumeComputedProperty(computedAttribute, propertyNode, computedAttributeData);
            }
        }

        return rootNode.getChildren() != null ? rootNode.getChildren() : new LinkedHashMap<>();
    }

    @Override
    public StructDataAccessor getEntity() {
        return getEntity(indexes.keySet().stream().findFirst().orElse(null));
    }

    @Override
    public StructDataAccessor getEntity(String entityName) {
        if(!StringUtils.isEmpty(entityName)) {
            EntityNode node = (EntityNode) indexes.get(entityName);
            if(node != null) {
                return createStructDataAccessor(node);
            }
        }
        return null;
    }

    @Override
    public StructDataAccessor getEntityOrNew(String entityName, String dataId) {
        Guard.notEmpty(entityName, "the arguments entityName is required.");

        if(indexes.containsKey(entityName)) {
            return getEntity(entityName);
        }

        EntityNode entityNode = new EntityNode(entityName, ROOT_KEY).init(entityName, dataId);
        indexes.put(entityName, entityNode);
        return createStructDataAccessor(entityNode);
    }

    @Override
    public boolean hasEntity(String entityName) {
        return indexes.containsKey(entityName);
    }

    @Override
    public void saveChanges(Action1<ActionCommand> action) {
        Guard.notNull(action, "the arguments action is required.");
        if(actionCommandPackage.isEmpty()) {
            return;
        }

        ActionCommandPackage commands = actionCommandPackage;
        actionCommandPackage = new ActionCommandPackage();
        for(ActionCommand command : commands) {
            if(command != null) {
                action.apply(command);
            }
            AttributeData attributeData = command.getAttributeData();
            if(attributeData != null && attributeData.getStatus() == DataStatus.Temp) {
                attributeData.setStatus(DataStatus.Enabled);
            }
        }
    }

    @Override
    public int getActionCommandCount() {
        return actionCommandPackage.size();
    }

    protected StructDataAccessor createStructDataAccessor(EntityNode entityNode) {
        return new StructDataAccessor(entityNode, attributeBagOperator, attributeKey);
    }

    protected <TValue> void resumeComputedProperty(ComputedAttribute<TValue> computedAttribute, IndexNode propertyNode, AttributeData computedAttributeData) {
        IndexNode parentNode = propertyNode.getParentNode();
        StructDataAccessor structDataAccessor = new StructDataAccessor(parentNode, attributeBagOperator, attributeKey);
        ComputedWatcher<StructDataAccessor, TValue> watcher = new ComputedWatcher<>(structDataAccessor, attributeBagOperator.getDependency(), computedAttribute.getComputedFn());
        watcher.setUpdateAction(value -> {
            TValue oldValue = computedAttribute.getConvertor().convert(computedAttributeData.getPropertyValue());
            computedAttributeData.setPropertyValue(computedAttribute.getConvertor().toStringValue(value));
            attributeBagOperator.notifyChanged(parentNode, ActionType.Modify, computedAttributeData, oldValue);
        });
        propertyNode.setAttachment(ComputedWatcher.WATCHER_NAME, watcher);
    }

    protected void onNotifyChanged(IndexNode node, ActionType actionType, AttributeData data, Object oldValue) {
        ActionCommand command = new ActionCommand(actionType, data);
        if(actionType == ActionType.Delete) {
            if(data.getStatus() == DataStatus.Temp) {
                actionCommandPackage.remove(command);
                return;
            }
        }
        // 指令采集
        actionCommandPackage.add(command);
        // 依赖通知
        onNotify(data.getKey(), node);
    }

    protected void onDepend(String key) {
        dependency.depend(key);
    }

    protected void onNotify(String key, IndexNode node) {
        Set<String> keys = new HashSet<>();
        keys.add(key);

        // 冒泡依赖通知
        IndexNode parentNode = node;
        while(parentNode != null) {
            keys.add(parentNode.getDependencyKey());
            parentNode = parentNode.getParentNode();
        }

        // 属性变更通知
        dependency.notify(keys);
    }

    @Override
    public DefaultAttributeJsonTemplate toJSON() {
        return new DefaultAttributeJsonTemplate(indexes, attributeBagOperator);
    }

}

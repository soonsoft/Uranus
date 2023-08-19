package com.soonsoft.uranus.core.common.attribute.access;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.common.attribute.access.IndexNode.*;
import com.soonsoft.uranus.core.common.attribute.data.AttributeData;
import com.soonsoft.uranus.core.common.attribute.data.AttributeKey;
import com.soonsoft.uranus.core.common.attribute.data.DataStatus;
import com.soonsoft.uranus.core.common.attribute.notify.Dependency;
import com.soonsoft.uranus.core.common.collection.MapUtils;
import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.core.functional.action.Action1;

public class AttributeBag {

    private final List<AttributeData> attributeDataList;
    private final AttributeBagOperator attributeBagOperator;
    private Map<String, IndexNode> indexes = null;
    private AttributeKey attributeKey = new AttributeKey();
    private Set<ActionCommand> actionCommandSet = new HashSet<>();
    private Dependency<String> dependency;
    private final static String ROOT_KEY = "__ROOT__";

    public AttributeBag(Dependency<String> dependency) {
        this(new ArrayList<>(), dependency);
    }

    public AttributeBag(List<AttributeData> attributeDataList, Dependency<String> dependency) {
        Guard.notNull(attributeDataList, "the arguments attributeDataList is required.");
        Guard.notNull(dependency, "the arguments dependency is required.");

        this.attributeDataList = attributeDataList;
        this.dependency = dependency;

        attributeBagOperator = initOperator(attributeDataList);
        indexes = initData();
    }

    protected AttributeBagOperator initOperator(final List<AttributeData> attributeDataList) {
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
        operator.setNotifyChangedFn((type, data, oldValue) -> onNotifyChanged(type, data, oldValue));
        operator.setCollectDependencyFn(key -> onDepend(key));
        return operator;
    }

    protected Map<String, IndexNode> initData() {
        if(attributeDataList == null || attributeDataList.isEmpty()) {
            return new LinkedHashMap<>();
        }

        Map<String, IndexNode> map = MapUtils.createHashMap(attributeDataList.size() + 10);
        RootNode rootNode = new RootNode(ROOT_KEY);

        int index = 0;
        for(AttributeData attributeData : attributeDataList) {
            String entityName = attributeData.getEntityName();
            String key = attributeData.getKey();
            String parentKey = 
                StringUtils.isEmpty(attributeData.getParentKey()) 
                    ? entityName 
                    : attributeData.getParentKey();
            String propertyName = attributeData.getPropertyName();

            IndexNode entityNode = map.get(entityName);
            if(entityNode == null) {
                entityNode = 
                    new EntityNode(entityName, ROOT_KEY)
                        .init(entityName, attributeData.getDataId());
                map.put(entityName, entityNode);
                rootNode.addChildNode(entityNode);
            }

            IndexNode parentNode = StringUtils.isEmpty(parentKey) ? entityNode : map.get(parentKey);
            if(parentNode == null) {
                parentNode = new TempIndexNode(key, parentKey);
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

            parentNode.addChildNode(node);

            index++;
        }
        return rootNode.getChildren() != null ? rootNode.getChildren() : new LinkedHashMap<>();
    }

    public StructDataAccessor getEntity() {
        return getEntity(indexes.keySet().stream().findFirst().orElse(null));
    }

    public StructDataAccessor getEntity(String entityName) {
        if(!StringUtils.isEmpty(entityName)) {
            EntityNode node = (EntityNode) indexes.get(entityName);
            if(node != null) {
                return createStructDataAccessor(node);
            }
        }
        return null;
    }

    public StructDataAccessor newEntity(String entityName) {
        if(indexes.containsKey(entityName)) {
            return getEntity(entityName);
        }

        EntityNode entityNode = new EntityNode(entityName, ROOT_KEY).init(entityName, entityName);
        indexes.put(entityName, entityNode);
        return createStructDataAccessor(entityNode);
    }

    public void saveChanges(Action1<ActionCommand> action) {
        Guard.notNull(action, "the arguments action is required.");
        if(actionCommandSet.isEmpty()) {
            return;
        }

        Set<ActionCommand> commands = actionCommandSet;
        actionCommandSet = new HashSet<>();
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

    public int getActionCommandCount() {
        return actionCommandSet.size();
    }

    protected StructDataAccessor createStructDataAccessor(EntityNode entityNode) {
        return new StructDataAccessor(entityNode, attributeBagOperator, attributeKey);
    }

    protected void onNotifyChanged(ActionType actionType, AttributeData data, Object oldValue) {
        ActionCommand command = new ActionCommand(actionType, data);
        if(actionType == ActionType.Delete) {
            if(data.getStatus() == DataStatus.Temp) {
                actionCommandSet.remove(command);
                return;
            }
        }
        actionCommandSet.add(command);
    }

    protected void onDepend(String key) {
        dependency.depend(key);
    }

}

package com.soonsoft.uranus.core.common.attribute.access;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.common.attribute.access.IndexNode.*;
import com.soonsoft.uranus.core.common.attribute.data.AttributeData;
import com.soonsoft.uranus.core.common.attribute.data.AttributeKey;
import com.soonsoft.uranus.core.common.collection.MapUtils;
import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.core.functional.action.Action2;
import com.soonsoft.uranus.core.functional.func.Func1;

public class AttributeBag {

    private final List<AttributeData> attributeDataList;
    private final Func1<Integer, AttributeData> attributeDataGetter;
    private final Action2<Integer, AttributeData> attributeDataSetter;
    private final Func1<AttributeData, Integer> attributeDataAdder;
    private final Action2<ActionType, AttributeData> actionCommandPicker;
    private Map<String, IndexNode> indexes = null;
    private AttributeKey attributeKey;

    public AttributeBag() {
        this(new ArrayList<>());
    }

    public AttributeBag(List<AttributeData> attributeDataList) {
        Guard.notNull(attributeDataList, "the arguments attributeDataList is requeired.");
        this.attributeDataList = attributeDataList;
        this.attributeDataGetter = index -> attributeDataList.get(index.intValue());
        this.attributeDataSetter = (index, attrData) -> attributeDataList.set(index.intValue(), attrData);
        this.attributeDataAdder = item -> {
            int index = attributeDataList.size();
            if(attributeDataList.add(item)) {
                return index;
            }
            return -1;
        };
        this.actionCommandPicker = (type, data) -> {

        };

        init();
    }

    protected void init() {
        if(attributeDataList == null || attributeDataList.isEmpty()) {
            return;
        }

        String rootKey = "__ROOT__";
        Map<String, IndexNode> map = MapUtils.createHashMap(attributeDataList.size() + 10);
        RootNode rootNode = new RootNode(rootKey);

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
                    new EntityNode(entityName, rootKey)
                        .initVirtualAttrData(entityName, attributeData.getDataId());
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
                    ListNode listNode = new ListNode(attributeKey.generate(), parentKey, propertyName);
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
        indexes = rootNode.getChildren();
    }

    public StructDataAccessor getEntity() {
        return indexes != null 
            ? getEntity(indexes.keySet().stream().findFirst().orElse(null)) 
            : null;
    }

    public StructDataAccessor getEntity(String entityName) {
        if(indexes != null || !StringUtils.isEmpty(entityName)) {
            
        }
        return null;
    }   

}

package com.soonsoft.uranus.core.common.attribute.access;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.common.attribute.access.IndexNode.*;
import com.soonsoft.uranus.core.common.attribute.data.AttributeData;
import com.soonsoft.uranus.core.common.collection.MapUtils;
import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.core.functional.func.Func1;

public class AttributeBag {

    private List<AttributeData> attributeDataList;
    private Func1<Integer, AttributeData> attributeDataGetter;
    private Func1<AttributeData, Integer> attributeDataAdder;
    private Map<String, IndexNode> indexes = null;

    public AttributeBag() {
        this(new ArrayList<>());
    }

    public AttributeBag(List<AttributeData> attributeDataList) {
        Guard.notNull(attributeDataList, "the arguments attributeDataList is requeired.");
        this.attributeDataList = attributeDataList;
        this.attributeDataGetter = index -> attributeDataList.get(index.intValue());

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
            String key = attributeData.getId();
            String parentKey = 
                StringUtils.isEmpty(attributeData.getParentId()) 
                    ? entityName 
                    : attributeData.getParentId();
            String propertyName = attributeData.getPropertyName();

            IndexNode entityNode = map.get(entityName);
            if(entityNode == null) {
                entityNode = 
                    new EntityNode(entityName, rootKey)
                        .createVirtualAttrData(entityName, attributeData.getDataId());
                map.put(entityName, entityNode);
                rootNode.addChildNode(entityNode);
            }

            IndexNode node = map.get(key);
            if(node == null) {
                node = new IndexNode(key, parentKey, propertyName, index);
                map.put(key, node);
            } else if(node instanceof TempIndexNode tempNode) {
                node = new IndexNode(
                    tempNode.getKey(), tempNode.getParentKey(), propertyName, index, tempNode.getChildren());
                map.put(key, node);
            } else {
                // 重复，合并为数组
                if(node instanceof ListNode listNode) {
                    IndexNode newNode = new IndexNode(key, parentKey, propertyName, index);
                    listNode.addChildNode(newNode);
                } else {
                    ListNode listNode = new ListNode(key, parentKey, propertyName);
                    listNode.addChildNode(node);
                    IndexNode newNode = new IndexNode(key, parentKey, propertyName, index);
                    listNode.addChildNode(newNode);
                    map.put(key, listNode);
                    // 用于更新 parentNode chlidren
                    node = listNode;
                }   
            }

            IndexNode parentNode = StringUtils.isEmpty(parentKey) ? entityNode : map.get(parentKey);
            if(parentNode == null) {
                parentNode = new TempIndexNode(key, parentKey);
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

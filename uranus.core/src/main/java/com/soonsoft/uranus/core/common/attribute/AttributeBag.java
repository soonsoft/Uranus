package com.soonsoft.uranus.core.common.attribute;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.common.attribute.access.AttributeDataAccessor;
import com.soonsoft.uranus.core.common.attribute.access.EntityDataAccessor;
import com.soonsoft.uranus.core.common.attribute.data.AttributeData;
import com.soonsoft.uranus.core.common.collection.MapUtils;
import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.core.functional.func.Func1;
import com.soonsoft.uranus.core.functional.func.Func2;

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
                entityNode = new EntityNode(entityName, rootKey);
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

    protected String getKey(String part1, String part2) {
        return (StringUtils.isEmpty(part1) ? "" : part1) + "::" + part2;
    }

    public EntityDataAccessor getEntity(String entityName) {
        return null;
    }
    
    public <TValue> TValue getValue(Attribute<TValue> attribute) {
        return null;
    }

    public void getArray() {

    }

    public void getStruct() {

    }

    static class IndexNode {
        // ID
        private final String key;
        // ParentID
        private final String parentKey;
        // PropertyName
        private String propertyName;
        private int index;
        private Map<String, IndexNode> children;

        IndexNode(String key, String parentKey, String propertyName, int index) {
            this.key = key;
            this.parentKey = parentKey;
            this.propertyName = propertyName;
            this.index = index;
        }

        IndexNode(String key, String parentKey, String propertyName, int index, Map<String, IndexNode> children) {
            this(key, parentKey, propertyName, index);
            this.children = children;
        }

        public String getKey() {
            return key;
        }

        public String getParentKey() {
            return parentKey;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public int getIndex() {
            return index;
        }

        public Map<String, IndexNode> getChildren() {
            return children;
        }
        protected void setChildren(Map<String, IndexNode> children) {
            this.children = children;
        }

        public void addChildNode(IndexNode node) {
            Guard.notNull(node, "the arguments node is required.");
            if(children == null) {
                children = new LinkedHashMap<>();
            }
            children.put(node.getPropertyName(), node);
        }
    }

    static class RootNode extends IndexNode {
        RootNode(String key) {
            super(key, null, null, -1);
        }
    }

    static class EntityNode extends IndexNode {
        EntityNode(String key, String parentKey) {
            super(key, parentKey, key, -1);
        }
    }

    static class ListNode extends IndexNode {
        ListNode(String key, String parentKey, String attributeCode) {
            super(key, parentKey, attributeCode, -1);
        }

        @Override
        public void addChildNode(IndexNode node) {
            Guard.notNull(node, "the arguments node is required.");
            if(getChildren() == null) {
                setChildren(new LinkedHashMap<>());
            }
            getChildren().put(String.valueOf(getChildren().size()), node);
        }
    }

    static class TempIndexNode extends IndexNode {
        TempIndexNode(String key, String parentKey) {
            super(key, parentKey, null, -1);
        }
    }

}

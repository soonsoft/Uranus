package com.soonsoft.uranus.core.common.attribute.access;

import java.util.LinkedHashMap;
import java.util.Map;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.common.attribute.data.AttributeData;

public class IndexNode {
    // ID
    private final String key;
    // ParentID
    private final String parentKey;
    // PropertyName
    private String propertyName;
    private int index;
    private Map<String, IndexNode> children;

    public IndexNode(String key, String parentKey, String propertyName, int index) {
        this.key = key;
        this.parentKey = parentKey;
        this.propertyName = propertyName;
        this.index = index;
    }

    public IndexNode(String key, String parentKey, String propertyName, int index, Map<String, IndexNode> children) {
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

    public IndexNode getChildNode(String propertyName) {
        return children != null ? children.get(propertyName) : null;
    }

    public boolean contains(String propertyName) {
        return children != null ? children.containsKey(propertyName) : false;
    }

    static class RootNode extends IndexNode {
        RootNode(String key) {
            super(key, null, null, -1);
        }
    }

    static class EntityNode extends IndexNode {
        private AttributeData virtualAttributeData;
        
        EntityNode(String key, String parentKey) {
            super(key, parentKey, key, -1);
            virtualAttributeData = new AttributeData();
        }

        public EntityNode initVirtualAttrData(String entityName, String dataId) {
            virtualAttributeData.setEntityName(entityName);
            virtualAttributeData.setDataId(dataId);
            return this;
        }

        public AttributeData getVirtualAttributeData() {
            return virtualAttributeData;
        }
    }

    static class ListNode extends IndexNode {
        ListNode(String key, String parentKey, String propertyName) {
            super(key, parentKey, propertyName, -1);
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

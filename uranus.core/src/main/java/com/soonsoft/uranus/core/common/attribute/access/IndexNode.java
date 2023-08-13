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

    public void removeChildNode(String propertyName) {
        Guard.notEmpty(propertyName, "the arguments propertyName is required.");
        if(children == null) {
            children.remove(propertyName);
        }
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

    static class VirtualNode<T extends VirtualNode<T>> extends IndexNode {
        private AttributeData virtualAttributeData;

        public VirtualNode(String key, String parentKey, String propertyName) {
            super(key, parentKey, propertyName, -1);
            virtualAttributeData = new AttributeData();
            virtualAttributeData.setPropertyName(propertyName);
        }

        @SuppressWarnings("unchecked")
        public T init(String entityName, String dataId) {
            virtualAttributeData.setEntityName(entityName);
            virtualAttributeData.setDataId(dataId);
            return (T) this;
        }

        public AttributeData getVirtualAttributeData() {
            return virtualAttributeData;
        }
        
    }

    static class EntityNode extends VirtualNode<EntityNode> {
        EntityNode(String key, String parentKey) {
            super(key, parentKey, key);
        }
    }

    static class ListNode extends VirtualNode<ListNode> {
        ListNode(String key, String parentKey, String propertyName) {
            super(key, parentKey, propertyName);
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

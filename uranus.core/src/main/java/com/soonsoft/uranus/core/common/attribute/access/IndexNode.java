package com.soonsoft.uranus.core.common.attribute.access;

import java.util.HashMap;
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
    private IndexNode parentNode;
    private Map<String, Object> attachmentMap;

    public IndexNode(String key, String parentKey, String propertyName, int index) {
        this.key = key;
        this.parentKey = parentKey;
        this.propertyName = propertyName;
        this.index = index;
    }

    public IndexNode(String key, String parentKey, String propertyName, int index, Map<String, IndexNode> children) {
        this(key, parentKey, propertyName, index);
        this.children = children;
        // 修正 parentNode 引用
        if(this.children != null) {
            for(Map.Entry<String ,IndexNode> entry : this.children.entrySet()) {
                entry.getValue().setParentNode(this);
            }
        }
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
        node.setParentNode(this);
    }

    public IndexNode getChildNode(String propertyName) {
        return children != null ? children.get(propertyName) : null;
    }

    public boolean contains(String propertyName) {
        return children != null ? children.containsKey(propertyName) : false;
    }

    protected void setParentNode(IndexNode parentNode) {
        this.parentNode = parentNode;
    }
    public IndexNode getParentNode() {
        return parentNode;
    }

    public String getDependencyKey() {
        return this.key;
    }

    public boolean setAttachment(String key, Object attachment) {
        Guard.notEmpty(key, "the arguments [key] is required.");
        if(attachment == null) {
            return false;
        }
        if(this.attachmentMap == null) {
            this.attachmentMap = new HashMap<>();
        }
        this.attachmentMap.put(key, attachment);
        return true;
    }

    public Object getAttachment(String key) {
        if(this.attachmentMap != null) {
            return this.attachmentMap.get(key);
        }
        return null;
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
            node.setParentNode(this);
        }

        @Override
        public String getDependencyKey() {
            return getParentKey() + "::" + getPropertyName();
        }
    }

    static class TempIndexNode extends IndexNode {
        TempIndexNode(String key, String parentKey) {
            super(key, parentKey, null, -1);
        }
    }
}

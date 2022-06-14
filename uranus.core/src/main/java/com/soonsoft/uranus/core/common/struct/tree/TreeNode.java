package com.soonsoft.uranus.core.common.struct.tree;

import java.util.List;

import com.soonsoft.uranus.core.error.argument.ArgumentNullException;

public class TreeNode<TData> {
    
    private String treeKey;

    private String treeParentKey;

    private TData treeData;

    private List<TreeNode<TData>> children;

    public String getTreeKey() {
        return treeKey;
    }

    public void setTreeKey(String treeKey) {
        this.treeKey = treeKey;
    }

    public String getTreeParentKey() {
        return treeParentKey;
    }

    public void setTreeParentKey(String treeParentKey) {
        this.treeParentKey = treeParentKey;
    }

    public TData getTreeData() {
        return treeData;
    }

    public void setTreeData(TData treeData) {
        this.treeData = treeData;
    }

    public List<TreeNode<TData>> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode<TData>> children) {
        this.children = children;
    }

    public void addChildNode(TreeNode<TData> child) {
        if(child == null) {
            throw new ArgumentNullException("child");
        }
        this.children.add(child);
    }

}

package com.soonsoft.uranus.core.common.struct.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.soonsoft.uranus.core.common.collection.MapUtils;
import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.core.functional.action.Action1;
import com.soonsoft.uranus.core.functional.behavior.IDeepEach;
import com.soonsoft.uranus.core.functional.func.Func1;
import com.soonsoft.uranus.core.functional.predicate.Predicate1;

public class TreeRoot<TData> extends TreeNode<TData> implements IDeepEach<TData> {

    public final static String ROOT_KEY = "ROOT_KEY";

    private Func1<TData, String> treeKeyGetter;
    
    private Func1<TData, String> treeParentKeyGetter;

    private Predicate1<String> isRootKeyFn;

    public TreeRoot(Func1<TData, String> treeKeyGetter, Func1<TData, String> treeParentKeyGetter) {
        this(treeKeyGetter, treeParentKeyGetter, parentKey -> parentKey == null);
    }

    public TreeRoot(Func1<TData, String> treeKeyGetter, Func1<TData, String> treeParentKeyGetter, Predicate1<String> isRootKeyFn) {
        this.treeKeyGetter = treeKeyGetter;
        this.treeParentKeyGetter = treeParentKeyGetter;
        this.isRootKeyFn = isRootKeyFn;
    }

    public void load(List<TData> list) {
        if(list == null || list.isEmpty()) {
            return;
        }

        Map<String, TreeNode<TData>> map = MapUtils.createHashMap(list.size());

        for(TData element : list) {
            String key = treeKeyGetter.call(element);
            String parentKey = treeParentKeyGetter.call(element);
            if(isRootKeyFn.test(parentKey)) {
                parentKey = ROOT_KEY;
            }

            TreeNode<TData> node = map.get(key);
            if(node == null) {
                node = new TreeNode<>();
            } else if(node instanceof TempVirtualTreeNode<TData> tempNode) {
                node = new TreeNode<>();
                node.setChildren(tempNode.getChildren());
            } else {
                throw new IllegalStateException(StringUtils.format("the treeKey[{0}] is exists.", key));
            }

            node.setTreeKey(key);
            node.setTreeParentKey(parentKey);
            node.setTreeData(element);
            map.put(key, node);

            TreeNode<TData> parentNode = map.get(parentKey);
            if(parentNode == null) {
                parentNode = 
                    ROOT_KEY.equals(parentKey)
                        ? this
                        : new TempVirtualTreeNode<TData>();
                parentNode.setTreeKey(parentKey);
                map.put(parentKey, parentNode);
            }

            if(parentNode.getChildren() == null) {
                parentNode.setChildren(new ArrayList<>());
            }
            parentNode.addChildNode(node);
        }
    }

    @Override
    public void deepEach(Action1<TData> fn) {
        if(this.getChildren() == null) {
            return;
        }

        Stack<TreeNode<TData>> stack = new Stack<>();
        for(int i = this.getChildren().size() - 1; i >= 0; i--) {
            stack.push(this.getChildren().get(i));
        }
        while(!stack.isEmpty()) {
            TreeNode<TData> node = stack.pop();
            fn.apply(node.getTreeData());

            if(node.getChildren() != null) {
                for(int i = node.getChildren().size() - 1; i >= 0; i--) {
                    stack.push(node.getChildren().get(i));
                }
            }
        }

    }

    private static class TempVirtualTreeNode<TData> extends TreeNode<TData> {

    }
}

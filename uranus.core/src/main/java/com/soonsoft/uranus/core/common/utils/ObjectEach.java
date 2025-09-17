package com.soonsoft.uranus.core.common.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.soonsoft.uranus.core.common.lang.ObjectUtils;
import com.soonsoft.uranus.core.common.lang.ObjectUtils.ArrayInfo;
import com.soonsoft.uranus.core.functional.action.Action1;
import com.soonsoft.uranus.core.functional.action.Action5;
import com.soonsoft.uranus.core.functional.behavior.IDeepEach;

public class ObjectEach implements IDeepEach<ObjectEach.EachNode> {

    private final Object target;
    private ObjectEach(Object target) {
        this.target = target;
    }

    @Override
    public void deepEach(Action1<EachNode> action) {
        if(target == null || action == null) {
            return;
        }

        RootNode root = new RootNode(null);
        EachNode node = selectNode(target, root, null);
        if(node instanceof ComplexNode) {
            Stack<ComplexNode> stack = new Stack<>();
            stack.push((ComplexNode) node);
            ObjectIdentitySet referenceSet = new ObjectIdentitySet();
            if(target != null) {
                referenceSet.add(target);
            }

            while (!stack.empty()) {
                ComplexNode complexNode = stack.pop();
                if(complexNode.getValue() != this.target) {
                    action.apply(complexNode);
                }

                complexNode.setActionFn((value, key, type, parent, fn) -> {
                    // 处理循环依赖
                    if(value != null && referenceSet.contains(value)) {
                        return;
                    }

                    EachNode itemNode = value != null ? this.selectNode(value, parent, key) : this.selectNode(type, parent, key);
                    if(itemNode instanceof ComplexNode) {
                        if(value != null) {
                            referenceSet.add(value);
                        }
                        stack.push((ComplexNode) itemNode);
                    } else {
                        if(fn != null) {
                            fn.apply(itemNode);
                        }
                    }
                });
                complexNode.forEach(action);
            }
        } else {
            action.apply(node);
        }
    }

    public void each(Action1<EachNode> action) {
        if(target == null || action == null) {
            return;
        }

        RootNode rootNode = new RootNode(null);
        EachNode node = selectNode(target, rootNode, null);
        if(node.isComplexNode()) {
            ComplexNode complexNode = (ComplexNode) node;
            complexNode.setActionFn((value, key, type, parent, fn) -> {
                EachNode childNode = value != null ? selectNode(value, parent, key) : selectNode(type, parent, key);
                if(fn != null) {
                    fn.apply(childNode);
                }
            });
            ((ComplexNode) node).forEach(action);
        } else {
            action.apply(node);
        }
    }

    protected EachNode selectNode(Object target, EachNode parent, String name) {
        if(ObjectUtils.isSimpleType(target)) {
            return new EachNode(parent, name, target);
        } else if(ObjectUtils.isEnumType(target)) {
            return new EnumNode(parent, name, (Enum<?>) target);
        } else if(ObjectUtils.isArray(target)) {
            return new ArrayNode(parent, name, target);
        } else if(ObjectUtils.isListType(target)) {
            return new ListNode(parent, name, target);
        } else if(ObjectUtils.isSetType(target)) {
            return new SetNode(parent, name, target);
        } else if(ObjectUtils.isMapType(target)) {
            return new MapNode(parent, name, target);
        } else{
            return new PropertyNode(parent, name, target);
        }
    }

    protected EachNode selectNode(Class<?> type, EachNode parent, String name) {
        if(type != null) {
            if(type.isArray()) {
                return new ArrayNode(parent, name, null);
            } else if(type.isEnum()) {
                return new EnumNode(parent, name, null);
            } else if(List.class.isAssignableFrom(type)) {
                return new ListNode(parent, name, null);
            } else if(Map.class.isAssignableFrom(type)) {
                return new MapNode(parent, name, null);
            } else if(Iterable.class.isAssignableFrom(type)) {
                return new SetNode(parent, name, null);
            }
        }
        return new EachNode(parent, name, null);
    }

    public static ObjectEach of(Object obj) {
        return new ObjectEach(obj);
    }

    //#region Single Node

    public static class EachNode {
        private EachNode parent;
        private String key;
        private Object target;

        public EachNode(Object target) {
            this(null, null, target);
        }

        public EachNode(EachNode parent, String key, Object target) {
            this.parent = parent;
            this.key = key;
            this.target = target;
        }

        public EachNode getParent() {
            return parent;
        }

        public String getKey() {
            return key;
        }

        public Object getValue() {
            return target;
        }

        public List<EachNode> getParentPath() {
            ArrayList<EachNode> parentNodes = new ArrayList<>();
            EachNode parent = this.getParent();
            while(parent != null && !(parent instanceof RootNode)) {
                parentNodes.add(parent);
                parent = parent.getParent();
            }
            Collections.reverse(parentNodes);
            return parentNodes;
        }

        public boolean isComplexNode() {
            return this instanceof ComplexNode;
        }

        protected Object getTarget() {
            return target;
        }
    }

    public static class EnumNode extends EachNode {
        private final Enum<?> enumValue;

        public EnumNode(Object target) {
            this(null, null, (Enum<?>) target);
        }

        public EnumNode(EachNode parent, String name, Enum<?> enumValue) {
            super(parent, name, enumValue);
            this.enumValue = enumValue;
        }

        @Override
        public Object getValue() {
            return enumValue;
        }

        public String getEnumName() {
            return enumValue.name();
        }

        public int getEnumOrdinal() {
            return enumValue.ordinal();
        }
    }

    public static class RootNode extends EachNode {
        public RootNode(Object target) {
            super(null, null, target);
        }
    }

    //#endregion

    //#region ComplexNode

    public static abstract class ComplexNode extends EachNode {
        private Action5<Object, String, Class<?>, EachNode, Action1<EachNode>> actionFn;

        public ComplexNode(EachNode parent, String name, Object target) {
            super(parent, name, target);
        }

        public abstract void forEach(Action1<EachNode> action);

        protected void doAction(Object value, String key, Class<?> type, EachNode parent, Action1<EachNode> action) {
            if(actionFn != null) {
                actionFn.apply(value, key, type, parent, action);
            } else if(action != null) {
                EachNode node = new EachNode(parent, key, value);
                action.apply(node);
            }
        }

        public void setActionFn(Action5<Object, String, Class<?>, EachNode, Action1<EachNode>> actionFn) {
            this.actionFn = actionFn;
        }
    }

    public static class PropertyNode extends ComplexNode {

        public PropertyNode(EachNode parent, String name, Object target) {
            super(parent, name, target);
        }

        @Override
        public void forEach(Action1<EachNode> action) {
            Object target = this.getTarget();
            if(target == null || action == null) {
                return;
            }

            Class<?> type = target.getClass();
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(type, Object.class);
                PropertyDescriptor[] properties = beanInfo.getPropertyDescriptors();
                for(PropertyDescriptor propertyDescriptor : properties) {
                    String name = propertyDescriptor.getName();
                    Method getMethod = propertyDescriptor.getReadMethod();
                    if(getMethod == null) {
                        continue;
                    }
                    getMethod.setAccessible(true);
                    Object value = getMethod.invoke(target);
                    Class<?> propertyClass = value != null ? value.getClass() : propertyDescriptor.getClass();
                    doAction(value, name, propertyClass, this, action);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class ArrayNode extends ComplexNode {
        public ArrayNode(EachNode parent, String name, Object target) {
            super(parent, name, target);
        }

        @Override
        public void forEach(Action1<EachNode> action) {
            Object target = this.getTarget();
            if(target == null || action == null) {
                return;
            }

            ArrayInfo arrayInfo = ObjectUtils.asArrayInfo(target);
            if(arrayInfo == null) {
                return;
            }

            arrayInfo.forEach((value, index, __) -> {
                String key = String.valueOf(index);
                Class<?> type = value != null ? value.getClass() : null;
                doAction(value, key, type, this, action);
            });
        }
    }

    public static class ListNode extends ComplexNode {
        public ListNode(EachNode parent, String name, Object target) {
            super(parent, name, target);
        }

        @Override
        public void forEach(Action1<EachNode> action) {
            Object target = this.getTarget();
            if(target == null || action == null) {
                return;
            }

            List<?> list = (List<?>) target;
            for(int i = 0, len = list.size(); i < len; i++) {
                Object value = list.get(i);
                String key = String.valueOf(i);
                Class<?> type = value != null ? value.getClass() : null;
                doAction(value, key, type, this, action);
            }
        }
    }

    public static class SetNode extends ComplexNode {
        public SetNode(EachNode parent, String name, Object target) {
            super(parent, name, target);
        }

        @Override
        public void forEach(Action1<EachNode> action) {
            Object target = this.getTarget();
            if(target == null || action == null) {
                return;
            }

            Iterable<?> set = (Iterable<?>) target;
            int index = 0;
            for(Object value : set) {
                String key = String.valueOf(index);
                Class<?> type = value != null ? value.getClass() : null;
                doAction(value, key, type, this, action);
                index++;
            }
        }
    }

    public static class MapNode extends ComplexNode {
        public MapNode(EachNode parent, String name, Object target) {
            super(parent, name, target);
        }

        @Override
        public void forEach(Action1<EachNode> action) {
            Object target = this.getTarget();
            if(target == null || action == null) {
                return;
            }

            Map<?, ?> map = (Map<?, ?>) target;
            for(Map.Entry<?, ?> entry : map.entrySet()) {
                Object keyObj = entry.getKey();
                String key = keyObj != null ? String.valueOf(keyObj) : "";
                Object value = entry.getValue();
                Class<?> type = value != null ? value.getClass() : null;
                doAction(value, key, type, this, action);
            }
        }
    }

    //#endregion

}

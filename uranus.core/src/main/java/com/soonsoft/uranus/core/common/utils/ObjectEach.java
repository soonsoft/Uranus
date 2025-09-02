package com.soonsoft.uranus.core.common.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigInteger;

import com.soonsoft.uranus.core.common.beans.BeanAccessorFactory;
import com.soonsoft.uranus.core.common.beans.BeanProperty;
import com.soonsoft.uranus.core.functional.action.Action1;
import com.soonsoft.uranus.core.functional.action.Action3;
import com.soonsoft.uranus.core.functional.behavior.ForEachBehavior;
import com.soonsoft.uranus.core.functional.behavior.IDeepEach;
import com.soonsoft.uranus.core.functional.behavior.IForEach;

public class ObjectEach implements IForEach<ObjectEach.EachNode>, IDeepEach<ObjectEach.EachNode> {

    private final Object target;
    public ObjectEach(Object target) {
        this.target = target;
    }

    @Override
    public void deepEach(Action1<EachNode> action) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deepEach'");
    }

    @Override
    public void forEach(Action3<EachNode, Integer, ForEachBehavior> action) {
        if(target == null || action == null) {
            return;
        }

        ForEachBehavior behavior = new ForEachBehavior();
        if(isSimpleType(target)) {
            EachNode node = new EachNode(target);
            action.apply(node, 0, behavior);
        } else if(isEnumType(target)) {

        } else {
            // BeanInfo beanInfo = Introspector.getBeanInfo(target.getClass(), Object.class);
            // PropertyDescriptor[] properties = beanInfo.getPropertyDescriptors();
            
            // for(PropertyDescriptor propertyDescriptor : properties) {
            //     String name = propertyDescriptor.getName();
            //     Object value = propertyDescriptor.getReadMethod().invoke(target);
            //     EachNode node = new PropertyNode(null, name, value, propertyDescriptor);
            //     behavior.reset();
            //     action.apply(node, 0, behavior);
            //     if(behavior.isContinue()) {
            //         continue;
            //     }
            //     if(behavior.isBreak()) {
            //         break;
            //     }
            // }
        }
    }

    protected EachNode selectEachNode(Object obj) {
        if(isSimpleType(obj)) {
            //return new PropertyNode();
        }
        return null;
    }

    protected boolean isSimpleType(Object obj) {
        if(obj == null) {
            return true;
        }
        Class<?> type = obj.getClass();
        return type.isPrimitive() || type == String.class || type == Boolean.class
                || Number.class.isAssignableFrom(type) || type == Character.class
                || type == BigInteger.class || type == java.math.BigDecimal.class
                || type == java.util.UUID.class
                || type == java.util.Date.class
                || type == java.time.LocalDate.class
                || type == java.time.LocalDateTime.class || type == java.time.LocalTime.class;
    }

    protected boolean isEnumType(Object obj) {
        if(obj == null) {
            return false;
        }
        Class<?> type = obj.getClass();
        return type.isEnum();
    }

    public static class EachNode {
        private EachNode parent;
        private String name;
        private Object target;

        public EachNode(Object target) {
            this(null, null, target);
        }

        public EachNode(EachNode parent, String name, Object target) {
            this.parent = parent;
            this.name = name;
            this.target = target;
        }

        public EachNode getParent() {
            return parent;
        }

        public String getName() {
            return name;
        }

        public Object getValue() {
            return target;
        }
    }

    public static class PropertyNode extends EachNode {
        private final PropertyDescriptor propertyDescriptor;

        public PropertyNode(EachNode parent, String name, Object target, PropertyDescriptor propertyDescriptor) {
            super(parent, name, target);
            this.propertyDescriptor = propertyDescriptor;
        }

        @Override
        public Object getValue() {
            return propertyDescriptor.getValue(getName());
        }
    }

}

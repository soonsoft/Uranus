package com.soonsoft.uranus.core.common.beans;

import java.beans.PropertyDescriptor;

import com.soonsoft.uranus.core.functional.action.Action2;
import com.soonsoft.uranus.core.functional.func.Func1;
import com.soonsoft.uranus.core.common.beans.error.BeanPropertyException;
import com.soonsoft.uranus.core.error.argument.ArgumentNullException;

public class BeanProperty<TObject, TValue> {
    
    private final PropertyDescriptor propertyDescriptor;

    private Func1<Object, Object> getter;
    
    private Action2<Object, TValue> setter;

    @SuppressWarnings("unchecked")
    public BeanProperty(PropertyDescriptor propertyDescriptor) {
        if(propertyDescriptor == null) {
            throw new ArgumentNullException("propertyDescriptor");
        }
        this.propertyDescriptor = propertyDescriptor;

        if(propertyDescriptor.getReadMethod() != null) {
            this.getter = (Func1<Object, Object>) DynamicMethodFactory.getVirtualMethodHandler(Func1.class, propertyDescriptor.getReadMethod());
        }
        if(propertyDescriptor.getWriteMethod() != null) {
            this.setter = (Action2<Object, TValue>) DynamicMethodFactory.getVirtualMethodHandler(Action2.class, propertyDescriptor.getWriteMethod());
        }
    }

    @SuppressWarnings("unchecked")
    public TValue get(TObject instance) {
        if(getter == null) {
            throw new BeanPropertyException("the parameter [%s] cannot call getter.", getPropertyName());
        }
        return (TValue) getter.call(instance);
    }

    public void set(TObject instance, TValue value) {
        setter.apply(instance, value);
    }

    public String getPropertyName() {
        // baseName = NameGenerator.capitalize(getName());
        return propertyDescriptor.getName();
    }

}

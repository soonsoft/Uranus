package com.soonsoft.uranus.core.common.beans;

import java.beans.PropertyDescriptor;

import com.soonsoft.uranus.core.functional.action.Action1;
import com.soonsoft.uranus.core.functional.func.Func1;

public class BeanProperty<TObject, TValue> {
    
    private final PropertyDescriptor propertyDescriptor;

    private Func1<Object, Object> getter;
    
    private Action1<TValue> setter;

    public BeanProperty(PropertyDescriptor propertyDescriptor) {
        this.propertyDescriptor = propertyDescriptor;
    }

    @SuppressWarnings("unchecked")
    public TValue get(TObject instance) {
        return (TValue) getter.call(instance);
    }

    public void set(TObject instance, TValue value) {
        setter.apply(value);
    }

    public String getPropertyName() {
        return propertyDescriptor.getName();
    }

    private void initGetter() {
        //propertyDescriptor.getReadMethod().getReturnType()
    }

}

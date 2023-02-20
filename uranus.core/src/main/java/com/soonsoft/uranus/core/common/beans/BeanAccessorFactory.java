package com.soonsoft.uranus.core.common.beans;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import com.soonsoft.uranus.core.common.beans.error.BeanAccessorException;
import com.soonsoft.uranus.core.error.argument.ArgumentNullException;

public class BeanAccessorFactory {

    public static <T> BeanAccessor<T> create(Class<T> beanClass) {
        if(beanClass == null) {
            throw new ArgumentNullException("beanClass");
        }
        BeanAccessor<T> accessor = new BeanAccessor<>();

        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(beanClass, Object.class);
            PropertyDescriptor[] properties = beanInfo.getPropertyDescriptors();
            
            for(PropertyDescriptor propertyDescriptor : properties) {
                BeanProperty<T, Object> property = new BeanProperty<>(propertyDescriptor);
                accessor.add(property);
            }

            return accessor;
        } catch(Throwable e) {
            throw new BeanAccessorException("create BeanAccessor error.", e);
        }
    }
    
}

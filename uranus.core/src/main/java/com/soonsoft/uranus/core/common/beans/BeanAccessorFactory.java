package com.soonsoft.uranus.core.common.beans;

public class BeanAccessorFactory {

    public static <T> BeanAccessor<T> create(Class<T> beanClass) {

        //beanClass.getDeclaredFields()

        BeanAccessor<T> accessor = new BeanAccessor<>();

        return accessor;
    }


    
}

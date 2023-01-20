package com.soonsoft.uranus.core.common.beans;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import org.junit.Assert;
import org.junit.Test;

public class BeanPropertyTest {

    @Test
    public void test_PropertyDescriptor() throws Throwable {
        BeanInfo beanInfo = Introspector.getBeanInfo(TestBean.class, Object.class);
        PropertyDescriptor[] properties = beanInfo.getPropertyDescriptors();
        assert properties.length == 5;

        for(int i = 0; i < properties.length; i++) {
            PropertyDescriptor property = properties[i];
            if(property.getName().equals("Name")) {
                Assert.assertNull(property.getWriteMethod());
            }
        }
        
    }

}

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
            System.out.println(String.format("%s, %s", property.getDisplayName(), property.getName()));
            if(property.getName().equals("Name")) {
                Assert.assertNull(property.getWriteMethod());
            }
        }
    }

    public void test_BeanProperty() {

    }

    @Test
    public void test_BeanAccessor() {
        BeanAccessor<TestBean> testBeanAccessor = BeanAccessorFactory.create(TestBean.class);
        
        TestBean testBean = new TestBean();

        testBeanAccessor.set(testBean, "intValue", 1);
        Assert.assertTrue(testBeanAccessor.get(testBean, "intValue").equals(1));

        testBeanAccessor.set(testBean, "boolValue", true);
        Assert.assertTrue(testBeanAccessor.get(testBean, "boolValue").equals(true));

        //testBeanAccessor.set(testBean, "name", "周星星");
        Assert.assertTrue(testBeanAccessor.get(testBean, "name").equals("周星星"));

        testBeanAccessor.set(testBean, "strValue", "无厘头");
        Assert.assertTrue(testBeanAccessor.get(testBean, "strValue").equals("无厘头"));

        String[] arr = new String[] { "大话西游", "鹿鼎记", "九品芝麻官" };
        testBeanAccessor.set(testBean, "arrValue", arr);
        String[] arrValue = testBeanAccessor.get(testBean, "arrValue");
        Assert.assertTrue(arrValue == arr);
        
    }

}

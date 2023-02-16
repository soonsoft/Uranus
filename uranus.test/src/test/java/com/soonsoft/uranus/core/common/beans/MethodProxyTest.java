package com.soonsoft.uranus.core.common.beans;

import org.junit.Assert;
import org.junit.Test;

import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.core.functional.func.Func1;
import com.soonsoft.uranus.core.functional.func.Func3;

public class MethodProxyTest {
    
    @Test
    @SuppressWarnings("unchecked")
    public void test_getterAndSetter() throws Throwable {
        MethodProxy methodProxy = new MethodProxy();
        MethodProxyTest instance = new MethodProxyTest();

        Func1<Object, Object> boolValueGetter = 
            methodProxy.getVirtualProxy(Func1.class, this.getClass().getMethod("getBoolValue"));
        Assert.assertTrue((boolean) boolValueGetter.call(instance));

        Func3<Object, Object, Object, Object> intValueSetter = 
            methodProxy.getVirtualProxy(
                Func3.class, 
                this.getClass().getMethod("setIntValue", Integer.class, Integer.class));

        // Method method = intValueSetter.getClass().getDeclaredMethod("writeReplace");
        // method.setAccessible(true);
        // SerializedLambda serializedLambda = (SerializedLambda)method.invoke(intValueSetter);
        
        Object value = intValueSetter.call(instance, 1, 2);
        Assert.assertTrue(Integer.valueOf(3).equals(value));
    }

    public boolean getBoolValue() {
        return true;
    }

    public Integer setIntValue(Integer num1, Integer num2) {
        System.out.println(StringUtils.format("num1 = %d, num2 = %d", num1, num2));
        return num1 + num2;
    }

}

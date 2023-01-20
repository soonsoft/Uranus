package com.soonsoft.uranus.core.common.beans;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

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
            methodProxy.getVirtualProxy(Func1.class, MethodProxyTest.class, this.getClass().getMethod("getBoolValue"));
        Assert.assertTrue((boolean) boolValueGetter.call(instance));

        Func3<Object, Object, Object, Object> intValueSetter = 
            methodProxy.getVirtualProxy(
                Func3.class, 
                MethodProxyTest.class, 
                this.getClass().getMethod("setIntValue", int.class, int.class));

        // Method method = intValueSetter.getClass().getDeclaredMethod("writeReplace");
        // method.setAccessible(true);
        // SerializedLambda serializedLambda = (SerializedLambda)method.invoke(intValueSetter);
        
        intValueSetter.call(instance, 1, 2);
    }

    public boolean getBoolValue() {
        return true;
    }

    public int setIntValue(int num1, int num2) {
        System.out.println(StringUtils.format("num1 = %d, num2 = %d", num1, num2));
        return num1 + num2;
    }

}

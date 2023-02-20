package com.soonsoft.uranus.core.common.beans;

import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;

import com.soonsoft.uranus.core.common.lang.StringUtils;
import com.soonsoft.uranus.core.functional.func.Func1;
import com.soonsoft.uranus.core.functional.func.Func2;
import com.soonsoft.uranus.core.functional.func.Func3;

public class MethodProxyTest {
    
    @Test
    @SuppressWarnings("unchecked")
    public void test_getterAndSetter() throws Throwable {
        MethodProxyTest instance = new MethodProxyTest();

        Method getBoolValue = MethodProxyTest.class.getMethod("getBoolValue");
        Func1<MethodProxyTest, Object> boolValueGetter = DynamicMethodFactory.getVirtualMethodHandler(Func1.class, getBoolValue);
        Assert.assertTrue((boolean) boolValueGetter.call(instance));

        Method setIntegerValue = MethodProxyTest.class.getMethod("setIntegerValue", Integer.class, Integer.class);
        Func3<MethodProxyTest, Integer, Integer, Integer> intValueSetter = DynamicMethodFactory.getVirtualMethodHandler(Func3.class, setIntegerValue);
        Integer value = intValueSetter.call(instance, 4, 5);
        Assert.assertTrue(Integer.valueOf(9).equals(value));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void test_staticMethond() throws Throwable {
        Method addMethod = MethodProxyTest.class.getMethod("add", Integer.class, Integer.class);
        Func2<Integer, Integer, Integer> addFunc = DynamicMethodFactory.getStaticMethodHandler(Func2.class, addMethod);
        Assert.assertTrue(addFunc.call(1, 1).equals(2));
    }

    public boolean getBoolValue() {
        return true;
    }

    public Integer setIntegerValue(Integer num1, Integer num2) {
        System.out.println(StringUtils.format("num1 = %d, num2 = %d", num1, num2));
        return num1 + num2;
    }

    public static Integer add(Integer a, Integer b) {
        return a + b;
    }

}

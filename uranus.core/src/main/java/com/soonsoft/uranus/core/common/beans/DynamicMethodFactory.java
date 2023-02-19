package com.soonsoft.uranus.core.common.beans;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Arrays;

import com.soonsoft.uranus.core.common.beans.error.MethodProxyException;

public class DynamicMethodFactory {

    /**
     * 获取 Method 对象的代理 Lambda，用于绑定调用而非反射调用
     * @param <R> 范围 Lambda 类型，仅支持 Func0 ~ Func9 or Action0 ~ Action9
     * @param funcClass Func or Action 的 Class，与<R> 对应
     * @param method 需要代理的 Method 对象
     * @return 代理 Lambda
     */
    public static <R> R createLambdaMethodHandler(Class<R> funcClass, Method method) {
        Class<?> instanceClass = method.getDeclaringClass();
        MethodHandles.Lookup lookup = MethodHandles.lookup();

        Class<?> resultClass = method.getReturnType();
        Class<?>[] parameterClasses = method.getParameterTypes();

        Class<?>[] lambdaParameterClasses = new Class<?>[parameterClasses.length];
        Arrays.setAll(lambdaParameterClasses, index -> Object.class);

        String interfaceMethodName = funcClass.getSimpleName().startsWith("Action") ? "apply" : "call";

        try {
            MethodHandle methodHandle = lookup.unreflect(method);
            CallSite site = LambdaMetafactory.metafactory(
                lookup, 
                interfaceMethodName, 
                MethodType.methodType(funcClass), 
                MethodType.methodType(resultClass == void.class ? resultClass : Object.class, Object.class, lambdaParameterClasses),
                methodHandle,
                MethodType.methodType(resultClass, instanceClass, parameterClasses));
            return (R) site.getTarget().invoke();
        } catch(Throwable e) {
            throw new MethodProxyException("create virtual method proxy error.", e);
        }
    }

    public static void getVirtualMethodHandler() {
        
    }

}

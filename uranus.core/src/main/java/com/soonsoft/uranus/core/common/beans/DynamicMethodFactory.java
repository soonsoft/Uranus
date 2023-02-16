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

    public static <R> R createLambdaMethodHandler(Class<R> funcClass, Method method) {
        Class<?> instanceClass = method.getDeclaringClass();
        MethodHandles.Lookup lookup = MethodHandles.lookup();

        Class<?> resultClass = method.getReturnType();
        Class<?>[] parameterClasses = method.getParameterTypes();

        Class<?>[] lambdaParameterClasses = new Class<?>[parameterClasses.length];
        Arrays.setAll(lambdaParameterClasses, index -> parameterClasses[index]);

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

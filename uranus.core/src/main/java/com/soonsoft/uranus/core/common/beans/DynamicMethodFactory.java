package com.soonsoft.uranus.core.common.beans;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import com.soonsoft.uranus.core.common.beans.error.MethodProxyException;
import com.soonsoft.uranus.core.error.argument.ArgumentNullException;

public class DynamicMethodFactory {

    /**
     * 获取实例方法的代理 Lambda，用于绑定调用而非反射调用，以获得接近原生调用的性能
     * @param <R> 范围 Lambda 类型，仅支持 Func0 ~ Func9 or Action0 ~ Action9
     * @param funcClass Func or Action 的 Class，与<R> 对应
     * @param method 需要代理的 Method 对象
     * @return Lambda 实例
     */
    public static <R> R getVirtualMethodHandler(Class<R> funcClass, Method method) {
        if(method == null) {
            throw new ArgumentNullException("method");
        }
        if(Modifier.isStatic(method.getModifiers())) {
            throw new MethodProxyException("the parameter is not virtual method.");
        }

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

    /**
     * 获取静态方法的代理 Lambda，用于绑定调用而非反射调用，以获得接近原生调用的性能
     * @param <R> 范围 Lambda 类型，仅支持 Func0 ~ Func9 or Action0 ~ Action9
     * @param funcClass Func or Action 的 Class，与<R> 对应
     * @param method 需要代理的 Method 对象
     * @return Lambda 实例
     */
    public static <R> R getStaticMethodHandler(Class<R> funcClass, Method method) {
        if(method == null) {
            throw new ArgumentNullException("method");
        }
        if(!Modifier.isStatic(method.getModifiers())) {
            throw new MethodProxyException("the parameter is not static method.");
        }

        Class<?> resultClass = method.getReturnType();
        Class<?>[] parameterClasses = method.getParameterTypes();

        Class<?>[] lambdaParameterClasses = new Class<?>[parameterClasses.length];
        Arrays.setAll(lambdaParameterClasses, index -> Object.class);

        String interfaceMethodName = funcClass.getSimpleName().startsWith("Action") ? "apply" : "call";

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            MethodHandle methodHandle = lookup.unreflect(method);
            CallSite site = LambdaMetafactory.metafactory(
                lookup, 
                interfaceMethodName, 
                MethodType.methodType(funcClass), 
                MethodType.methodType(resultClass == void.class ? resultClass : Object.class, lambdaParameterClasses),
                methodHandle,
                MethodType.methodType(resultClass, parameterClasses));
            return (R) site.getTarget().invoke();
        } catch(Throwable e) {
            throw new MethodProxyException("create virtual method proxy error.", e);
        }
    }

}

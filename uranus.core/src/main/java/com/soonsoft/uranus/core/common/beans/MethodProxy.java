package com.soonsoft.uranus.core.common.beans;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Arrays;

import com.soonsoft.uranus.core.common.beans.error.MethodProxyException;

public class MethodProxy {

    private MethodHandles.Lookup lookup;

    public MethodProxy() {
        this.lookup = MethodHandles.lookup();
    }

    public MethodProxy(MethodHandles.Lookup lookup) {
        this.lookup = lookup;
    }
    
    public <R> R getVirtualProxy(Class<R> funcClass, Class<?> instanceClass, Method method) {
        String methodName = method.getName();
        
        Class<?>[] methodParameterTypes = method.getParameterTypes();
        Class<?> returnType = method.getReturnType();

        Class<?>[] lambdaParameterTypes = new Class<?>[methodParameterTypes.length + 1];
        Arrays.setAll(lambdaParameterTypes, idx -> idx == 0 ? Object.class : methodParameterTypes[idx - 1]);

        Class<?>[] dynamicParameterTypes = new Class<?>[lambdaParameterTypes.length];
        Arrays.setAll(dynamicParameterTypes, idx -> idx == 0 ? instanceClass : methodParameterTypes[idx - 1]);

        try {
            CallSite site = LambdaMetafactory.metafactory(
                lookup, 
                "call", 
                MethodType.methodType(funcClass), 
                MethodType.methodType(returnType == void.class ? returnType : Object.class, lambdaParameterTypes),
                lookup.findVirtual(instanceClass, methodName, MethodType.methodType(returnType, methodParameterTypes)),
                MethodType.methodType(method.getReturnType(), dynamicParameterTypes));
            return (R) site.getTarget().invoke();
        } catch(Throwable e) {
            throw new MethodProxyException("create virtual method proxy error.", e);
        }
    }
}

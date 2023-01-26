package com.soonsoft.uranus.test;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Date;

import org.junit.Assert;

import com.soonsoft.uranus.core.functional.action.Action2;
import com.soonsoft.uranus.core.functional.func.Func1;
import com.soonsoft.uranus.core.functional.func.Func3;
import com.soonsoft.uranus.services.workflow.model.FlowActionParameter;

public class LambdaMetaFactoryTest {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Throwable {

        FlowActionParameter param = new FlowActionParameter();

        Method setOperatorMethod = FlowActionParameter.class.getMethod("setOperator", String.class);
        Action2<Object, String> operatorSetter = (Action2<Object, String>) createVirtual(Action2.class, setOperatorMethod);

        Method getOperatorMethod = FlowActionParameter.class.getMethod("getOperator");
        Func1<Object, String> operatorGetter = (Func1<Object, String>) createVirtual(Func1.class, getOperatorMethod);

        operatorSetter.apply(param, "eater");
        System.out.println(operatorGetter.call(param));


        Method addMethod = LambdaMetaFactoryTest.class.getMethod("add", Integer.class, Integer.class);
        Func3<LambdaMetaFactoryTest, Integer, Integer, Integer> addDelegate = createVirtual(Func3.class, addMethod);

        LambdaMetaFactoryTest test = new LambdaMetaFactoryTest();
        System.out.println(addDelegate.call(test, 1, 2));

        Method subtractMethod = LambdaMetaFactoryTest.class.getMethod("subtruct", Integer.class, Integer.class);
        Func3<LambdaMetaFactoryTest, Integer, Integer, Integer> subtractDelegate = createVirtual(Func3.class, subtractMethod);
        System.out.println(subtractDelegate.call(test, 5, 1));

    }

    public Integer add(Integer a, Integer b) {
        return a + b;
    }

    public Integer subtruct(Integer a, Integer b) {
        return a - b;
    }

    public static void testSpeed() throws Throwable {
        FlowActionParameter param = new FlowActionParameter();
        param.setOperatorName("大胃王");
        param.setOperator("eater");
        param.setOperateTime(new Date());

        long timestamp = System.currentTimeMillis();

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        CallSite site = LambdaMetafactory.metafactory(
            lookup, 
            "call", 
            MethodType.methodType(Func1.class), 
            MethodType.methodType(Object.class, Object.class),
            lookup.findVirtual(FlowActionParameter.class, "getOperatorName", MethodType.methodType(String.class)),
            MethodType.methodType(String.class, FlowActionParameter.class));
        Func1<Object, Object> func1 = (Func1<Object, Object>) site.getTarget().invokeExact();

        System.out.println("创建时间：" + (System.currentTimeMillis() - timestamp) + "ms");
        
        timestamp = System.currentTimeMillis();
        for(int i = 0; i < 100000; i++) {
            func1.call(param);
        }
        System.out.println("Lambda调用时间：" + (System.currentTimeMillis() - timestamp) + "ms");

        timestamp = System.currentTimeMillis();
        for(int i = 0; i < 100000; i++) {
            param.getOperatorName();
        }
        System.out.println("原生调用时间：" + (System.currentTimeMillis() - timestamp) + "ms");
    }

    public static abstract class DynamicFunction<T, R> {

        private final Func1<Object, Object> func;
        private final MethodHandle delegate;

        @SuppressWarnings("unchecked")
        public DynamicFunction(String methodName) {
            Type superClass = this.getClass().getGenericSuperclass();
            if(superClass instanceof Class) {
                throw new IllegalArgumentException("without actual type.");
            }
            Type[] typeArgs = ((ParameterizedType) superClass).getActualTypeArguments();
            Class<T> instanceClass = (Class<T>) typeArgs[0];
            Class<R> resultClass = (Class<R>) typeArgs[1];

            try {
                MethodHandles.Lookup lookup = MethodHandles.lookup();
                delegate = lookup.findVirtual(instanceClass, methodName, MethodType.methodType(resultClass));
                CallSite site = LambdaMetafactory.metafactory(
                    lookup, 
                    "call", 
                    MethodType.methodType(Func1.class), 
                    MethodType.methodType(Object.class, Object.class),
                    delegate,
                    MethodType.methodType(resultClass, instanceClass));
                func = (Func1<Object, Object>) site.getTarget().invoke();
            } catch(Throwable e) {
                throw new IllegalStateException(e);
            }
        }

        @SuppressWarnings("unchecked")
        public R call(T instance) {
            return (R) func.call(instance);
        }

        @SuppressWarnings("unchecked")
        public R apply(T instance) throws Throwable {
            FlowActionParameter p = (FlowActionParameter) instance;
            String result = (String) delegate.invokeExact(p);
            return (R) result;
        }
    }

    private static void testDynamicFunction() {
        FlowActionParameter param = new FlowActionParameter();
        param.setOperatorName("大胃王");
        param.setOperator("eater");
        param.setOperateTime(new Date());

        DynamicFunction<FlowActionParameter, String> df = new DynamicFunction<>("getOperator"){};
        Assert.assertTrue(param.getOperator().equals(df.call(param)));
        System.out.println(df.call(param));
    }

    //#region LambdaFactory

    private static Class<?> getParameterClass(Class<?> paramType) {
        if(paramType == int.class) {
            return Integer.class;
        }

        return Object.class;
    }

    public static <R> R createVirtual(Class<R> funcClass, Method method) {
        Class<?> instanceClass = method.getDeclaringClass();
        MethodHandles.Lookup lookup = MethodHandles.lookup();

        Class<?> resultClass = method.getReturnType();
        Class<?>[] parameterClasses = method.getParameterTypes();

        Class<?>[] lambdaParameterClasses = new Class<?>[parameterClasses.length];
        Arrays.setAll(lambdaParameterClasses, index -> getParameterClass(parameterClasses[index]));

        //MethodType type = MethodType.methodType(resultClass, instanceClass, lambdaParameterClasses);
        // if(lambdaParameterClasses.length > 0) {
        //     type.dropParameterTypes(0, lambdaParameterClasses.length);
        // }

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
            throw new RuntimeException("createVirtual error.", e);
        }
    }

    //#endregion
    
}

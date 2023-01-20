package com.soonsoft.uranus.test;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;

import com.soonsoft.uranus.core.functional.func.Func1;
import com.soonsoft.uranus.services.workflow.model.FlowActionParameter;

public class LambdaMetaFactoryTest {

    public static void main(String[] args) throws Throwable {

        FlowActionParameter param = new FlowActionParameter();
        param.setOperatorName("大胃王");
        param.setOperator("eater");
        param.setOperateTime(new Date());
        
        DynamicFunction<FlowActionParameter, String> df = new DynamicFunction<>("getOperator"){};
        System.out.println(df.call(param));

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
                //delegate = lookup.findGetter(instanceClass, methodName, resultClass);
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
    
}

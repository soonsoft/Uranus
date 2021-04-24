package com.soonsoft.uranus.core.common.lang;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import com.soonsoft.uranus.core.error.TypeInstantiationException;

/**
 * ClassHelper
 */
public abstract class TypeUtils {

    private static final Map<Class<?>, Object> DEFAULT_TYPE_VALUES;

    static {
        Map<Class<?>, Object> values = new HashMap<>();
        values.put(boolean.class, Boolean.FALSE);
        values.put(byte.class, (byte)0);
        values.put(short.class, (byte)0);
        values.put(int.class, 0);
        values.put(long.class, 0L);
        values.put(float.class, 0.0f);
        values.put(double.class, 0.0);

        DEFAULT_TYPE_VALUES = values;
    }

    /**
     * get class loader
     *
     * @param clazz
     * @return class loader
     */
    public static ClassLoader getClassLoader(Class<?> clazz) {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back to system class loader...
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = clazz.getClassLoader();
            if (cl == null) {
                // getClassLoader() returning null indicates the bootstrap ClassLoader
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Throwable ex) {
                    // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
                }
            }
        }

        return cl;
    }

    public static void makeAccessible(Constructor<?> ctor) {
        if ((!Modifier.isPublic(ctor.getModifiers()) 
                || !Modifier.isPublic(ctor.getDeclaringClass().getModifiers())) 
                && !ctor.trySetAccessible()) {
            ctor.setAccessible(true);
        }
    }

    public static <T> T createInstance(Class<T> clazz) {
        if(clazz == null) {
            throw new IllegalArgumentException("Class must not be null");
        }
        try {
            return createInstance(clazz.getDeclaredConstructor());
        } catch (NoSuchMethodException e) {
            throw new TypeInstantiationException(clazz, "No default constructor found", e);
        }
    }

    public static <T> T createInstance(Constructor<T> ctor, Object... constructorArgs) {
        if(ctor == null) {
            throw new IllegalArgumentException("Class must not be null");
        }
        if(constructorArgs == null) {
            constructorArgs = new Object[0];
        }
        try {
            makeAccessible(ctor);
            Class<?>[] parameterTypes = ctor.getParameterTypes();
            if(constructorArgs.length > parameterTypes.length) {
                throw new IllegalArgumentException("Can't specify more arguments than constructor parameters");
            }
            Object[] argsWithDefaultValues = new Object[constructorArgs.length];
            for (int i = 0 ; i < constructorArgs.length; i++) {
                if (constructorArgs[i] == null) {
                    Class<?> parameterType = parameterTypes[i];
                    argsWithDefaultValues[i] = (parameterType.isPrimitive() ? DEFAULT_TYPE_VALUES.get(parameterType) : null);
                }
                else {
                    argsWithDefaultValues[i] = constructorArgs[i];
                }
            }
            return ctor.newInstance(argsWithDefaultValues);
        } catch (InstantiationException ex) {
            throw new TypeInstantiationException(ctor, "Is it an abstract class?", ex);
        } catch (IllegalAccessException ex) {
            throw new TypeInstantiationException(ctor, "Is the constructor accessible?", ex);
        } catch (IllegalArgumentException ex) {
            throw new TypeInstantiationException(ctor, "Illegal arguments for constructor", ex);
        } catch (InvocationTargetException ex) {
            throw new TypeInstantiationException(ctor, "Constructor threw exception", ex.getTargetException());
        }
    }

}
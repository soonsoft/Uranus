package com.soonsoft.uranus.core.error;

import java.lang.reflect.Constructor;

public class TypeInstantiationException extends TypeException {

    private final Class<?> typeClass;

    private final Constructor<?> constructor;

    public TypeInstantiationException(Constructor<?> constructor, String message, Throwable e) {
        super(message, e);

        this.typeClass = constructor.getDeclaringClass();
        this.constructor = constructor;
    }

    public TypeInstantiationException(Class<?> typeClass, String message, Throwable e) {
        super(message, e);

        this.typeClass = typeClass;
        this.constructor = null;
    }

    public Class<?> getTypeClass() {
        return this.typeClass;
    }

    public Constructor<?> getConstructor() {
        return this.constructor;
    }
    
}

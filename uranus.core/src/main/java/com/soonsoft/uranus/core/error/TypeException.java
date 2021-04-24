package com.soonsoft.uranus.core.error;

public class TypeException extends ApplicationRuntimeException {
    
    public TypeException() {
        super();
    }

    public TypeException(String message) {
        this(message, null);
    }

    public TypeException(String message, Throwable e) {
        super(message, e);
    }

}

package com.soonsoft.uranus.core.error;

public class ApplicationRuntimeException extends RuntimeException {

    public ApplicationRuntimeException() {
        super();
    }

    public ApplicationRuntimeException(String message) {
        this(message, null);
    }

    public ApplicationRuntimeException(String message, Throwable e) {
        super(message, e);
    }
    
}

package com.soonsoft.uranus.core.common.beans.error;

public class MethodProxyException extends RuntimeException {

    public MethodProxyException(String message) {
        super(message);
    }

    public MethodProxyException(String message, Throwable e) {
        super(message, e);
    }
    
}

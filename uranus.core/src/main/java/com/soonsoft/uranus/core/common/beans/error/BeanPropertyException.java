package com.soonsoft.uranus.core.common.beans.error;

public class BeanPropertyException extends RuntimeException {

    public BeanPropertyException(String message, Object... param) {
        super(String.format(message, param));
    }

    public BeanPropertyException(String message, Throwable e) {
        super(message, e);
    }
    
}

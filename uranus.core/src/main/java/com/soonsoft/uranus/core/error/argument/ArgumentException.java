package com.soonsoft.uranus.core.error.argument;

public class ArgumentException extends IllegalArgumentException {

    public ArgumentException(Throwable e) {
        super(e.getMessage(), e);
    }
    
    public ArgumentException(String message, Object... params) {
        super(String.format(message, params));
    }

    public ArgumentException(String message, Throwable e) {
        super(message, e);
    }

    public ArgumentException(Throwable e, String message, Object... params) {
        super(String.format(message, params), e);
    }

}

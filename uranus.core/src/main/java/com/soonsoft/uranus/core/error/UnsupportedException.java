package com.soonsoft.uranus.core.error;

/**
 * UnsupportedException
 */
public class UnsupportedException extends ApplicationRuntimeException {

    public UnsupportedException() {
        super();
    }

    public UnsupportedException(String message) {
        super(message);
    }

    public UnsupportedException(String message, Throwable e) {
        super(message, e);
    }
}
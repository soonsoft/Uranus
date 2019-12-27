package com.soonsoft.uranus.core.error;

/**
 * UnsupportedException
 */
public class UnsupportedException extends RuntimeException {

    private static final long serialVersionUID = -3770247806444368835L;

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
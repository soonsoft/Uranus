package com.soonsoft.uranus.util.identity;

/**
 * IDGenerateException
 */
public class IDGenerateException extends RuntimeException {

    private static final long serialVersionUID = 123729160609499178L;

    public IDGenerateException(Exception e) {
        super(e);
    }

    public IDGenerateException(String message, Exception e) {
        super(message, e);
    }

    public IDGenerateException(String messageFormat, Object... args) {
        super(String.format(messageFormat, args));
    }

}
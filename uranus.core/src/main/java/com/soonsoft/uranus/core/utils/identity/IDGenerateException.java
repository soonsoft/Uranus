package com.soonsoft.uranus.core.utils.identity;

/**
 * IDGenerateException
 */
public class IDGenerateException extends RuntimeException {

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
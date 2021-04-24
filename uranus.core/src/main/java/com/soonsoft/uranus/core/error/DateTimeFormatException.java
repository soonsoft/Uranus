package com.soonsoft.uranus.core.error;

/**
 * DateTimeFormatException
 */
public class DateTimeFormatException extends ApplicationRuntimeException {

    public DateTimeFormatException() {
        super();
    }

    public DateTimeFormatException(String message) {
        super(message);
    }

    public DateTimeFormatException(String message, Throwable e) {
        super(message, e);
    }
    
}
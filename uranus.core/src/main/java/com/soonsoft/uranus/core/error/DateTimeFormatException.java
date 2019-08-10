package com.soonsoft.uranus.core.error;

/**
 * DateTimeFormatException
 */
public class DateTimeFormatException extends RuntimeException {

    private static final long serialVersionUID = 4320009820896206305L;

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
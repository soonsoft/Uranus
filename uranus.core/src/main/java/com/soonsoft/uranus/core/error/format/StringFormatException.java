package com.soonsoft.uranus.core.error.format;

public class StringFormatException extends FormatException {

    public StringFormatException() {
        super();
    }

    public StringFormatException(String message) {
        super(message);
    }

    public StringFormatException(String message, Throwable e) {
        super(message, e);
    }
    
}

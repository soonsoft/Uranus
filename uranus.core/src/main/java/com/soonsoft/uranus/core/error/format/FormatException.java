package com.soonsoft.uranus.core.error.format;

import com.soonsoft.uranus.core.error.ApplicationRuntimeException;

public class FormatException extends ApplicationRuntimeException {

    public FormatException() {
        super();
    }

    public FormatException(String message) {
        super(message);
    }

    public FormatException(String message, Throwable e) {
        super(message, e);
    }
    
}

package com.soonsoft.uranus.data.service;

import com.soonsoft.uranus.core.error.ApplicationRuntimeException;

public class DatabaseAccessException extends ApplicationRuntimeException {

    public DatabaseAccessException(String message) {
        this(message, null);
    }

    public DatabaseAccessException(String message, Throwable e) {
        super(message, e);
    }
    
}

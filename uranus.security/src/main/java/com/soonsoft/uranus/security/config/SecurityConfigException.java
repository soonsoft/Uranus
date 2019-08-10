package com.soonsoft.uranus.security.config;

/**
 * SecurityException
 */
public class SecurityConfigException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SecurityConfigException(String message) {
        super(message);
    }

    public SecurityConfigException(String message, Throwable e) {
        super(message, e);
    }
    
}
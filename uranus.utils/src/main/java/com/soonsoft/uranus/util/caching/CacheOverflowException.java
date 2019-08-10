package com.soonsoft.uranus.util.caching;

/**
 * CacheOverflowException
 */
public class CacheOverflowException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CacheOverflowException() {
        super();
    }

    public CacheOverflowException(String message) {
        super(message);
    }
}
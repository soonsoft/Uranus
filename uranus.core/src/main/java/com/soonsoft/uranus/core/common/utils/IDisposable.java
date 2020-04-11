package com.soonsoft.uranus.core.common.utils;

/**
 * IDisposable
 */
public interface IDisposable {

    /**
     * Destroy the component
     *
     * @throws IllegalStateException
     */
    void destroy() throws IllegalStateException;
    
}
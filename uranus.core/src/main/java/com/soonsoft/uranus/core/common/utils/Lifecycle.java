package com.soonsoft.uranus.core.common.utils;

/**
 * The Lifecycle of Component
 */
public interface Lifecycle extends IDisposable {

    /**
     * Initialize the component before {@link #start() start}
     *
     * @return current {@link Lifecycle}
     * @throws IllegalStateException
     */
    void initialize() throws IllegalStateException;

    /**
     * Start the component
     *
     * @return current {@link Lifecycle}
     * @throws IllegalStateException
     */
    void start() throws IllegalStateException;
    
}
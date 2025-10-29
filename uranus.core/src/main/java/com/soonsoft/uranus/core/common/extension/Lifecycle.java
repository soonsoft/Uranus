package com.soonsoft.uranus.core.common.extension;

/**
 * The Lifecycle of Component
 */
public interface Lifecycle {

    /**
     * Initialize the component
     *
     * @return current {@link Lifecycle}
     */
    void initialize();
    
}
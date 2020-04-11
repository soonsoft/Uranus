package com.soonsoft.uranus.security.authorization.event;

import java.util.function.Consumer;

/**
 * IFunctionChangedListener
 */
public interface IFunctionChangedListener<T> {

    void addFunctionChanged(Consumer<FunctionChangedEvent<T>> eventHandler);

    void removeFunctionChanged(Consumer<FunctionChangedEvent<T>> eventHandler);
    
}
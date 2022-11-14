package com.soonsoft.uranus.util.caching;

import java.util.function.Consumer;

public interface ICacheOperateListener<TEvent> {

    void addListener(String type, Consumer<TEvent> eventHandler);

    void removeListener(String type, Consumer<TEvent> eventHandler);

    void emit(String type, TEvent event);
    
}
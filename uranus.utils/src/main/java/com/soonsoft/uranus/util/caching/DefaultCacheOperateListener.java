package com.soonsoft.uranus.util.caching;

import java.util.function.Consumer;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.common.event.SimpleEventListener;
import com.soonsoft.uranus.core.error.UnsupportedException;

public class DefaultCacheOperateListener<TKey, TValue> 
        implements ICacheOperateListener<com.soonsoft.uranus.util.caching.DefaultCacheOperateListener.CacheEvent<TKey, TValue>> {

    public final static String Remove = "REMOVE_OPERATE";
    public final static String Insert = "INSERT_OPERATE";
    public final static String Update = "UPDATE_OPERATE";

    private SimpleEventListener<CacheEvent<TKey, TValue>> removeEvent;

    public DefaultCacheOperateListener() {
        removeEvent = new SimpleEventListener<>(Remove);
    }

    @Override
    public void addListener(String type, Consumer<CacheEvent<TKey, TValue>> eventHandler) {
        Guard.notNull(eventHandler, "the parameter eventHandler is required.");
        matchEvent(type).on(eventHandler);
    }

    @Override
    public void removeListener(String type, Consumer<CacheEvent<TKey, TValue>> eventHandler) {
        if(eventHandler != null) {
            matchEvent(type).off(eventHandler);
        }
    }

    @Override
    public void emit(String type, CacheEvent<TKey, TValue> event) {
        Guard.notNull(event, "the parameter event is required.");
        matchEvent(type).trigger(event);
    }
    
    public void emit(String type, TKey key, TValue value) {
        CacheEvent<TKey, TValue> event = new CacheEvent<>(type, key, value);
        emit(type, event);
    }

    protected SimpleEventListener<CacheEvent<TKey, TValue>> matchEvent(String type) {
        switch(type) {
            case Remove:
                return removeEvent;
            case Insert:
            case Update:
            default:
                throw new UnsupportedException("not supported this type[%s]", type);
        }
    }

    public static class CacheEvent<TKey, TValue> {
        private final String type;
        private final TKey key;
        private final TValue value;

        public CacheEvent(String type, TKey key, TValue value) {
            this.type = type;
            this.key = key;
            this.value = value;
        }

        public String getType() {
            return type;
        }

        public TKey getKey() {
            return key;
        }

        public TValue getValue() {
            return value;
        }
    }
    
}

package com.soonsoft.uranus.util.caching;

import com.soonsoft.uranus.core.functional.action.Action2;

public interface ICacheOperateListener<TKey, TValue> {

    void addListener(String type, Action2<TKey, TValue> onCacheRemoveAction);

    void removeListener(String type, Action2<TKey, TValue> onCacheRemoveAction);

    void emit(String type, TKey key, TValue value);
    
}
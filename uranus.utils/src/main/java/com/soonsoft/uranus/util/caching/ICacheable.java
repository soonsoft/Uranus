package com.soonsoft.uranus.util.caching;

import java.util.Map;
import java.util.Set;

/**
 * 缓存功能接口
 */
public interface ICacheable<TKey, TValue> {

    TValue get(TKey key);

    Map<TKey, TValue> get(Set<TKey> keys);
    
}
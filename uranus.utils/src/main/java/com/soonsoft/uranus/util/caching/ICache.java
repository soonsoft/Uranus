package com.soonsoft.uranus.util.caching;

import java.util.Map;
import java.util.Set;

/**
 * ICache
 */
public interface ICache<TKey, TValue> {

    /**
     * 获取缓存
     * @param key 缓存Key
     * @return
     */
    TValue get(TKey key);

    /**
     * 设置缓存
     * @param key 缓存Key
     * @param value 缓存Value
     */
    void put(TKey key, TValue value);

    /**
     * 批量设置缓存
     * @param map 要被设置的缓存Map集合
     */
    void putAll(Map<TKey, TValue> map);

    /**
     * 移除缓存
     * @param key 缓存Key
     * @return
     */
    boolean remove(TKey key);

    /**
     * 批量移除缓存
     * @param keys 缓存Key集合
     * @return
     */
    boolean remove(Set<TKey> keys);

}
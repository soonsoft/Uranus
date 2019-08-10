package com.soonsoft.uranus.util.caching;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 分组缓存功能
 */
public abstract class GroupCacheable<TGroupKey, TKey, TValue> extends Cacheable<TKey, TValue> {

    private ConcurrentHashMap<TGroupKey, ICache<TKey, TValue>> cacheGroup;

    private long expireTime = 0;

    private int capacity = 256;

    public GroupCacheable() {
        cacheGroup = new ConcurrentHashMap<>();
    }

    public GroupCacheable(int capacity, long expireTime) {
        this();
        this.capacity = capacity;
        this.expireTime = expireTime;
    }

    @Override
    protected ICache<TKey, TValue> getCache() {
        return cacheGroup.computeIfAbsent(getGroupKey(), k -> {
            Cache<TKey, TValue> cache = new Cache<>(capacity);
            cache.setExpireTime(expireTime);
            return cache;
        });
    }

    protected abstract TGroupKey getGroupKey();
    
}
package com.soonsoft.uranus.util.caching;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import com.soonsoft.uranus.core.Guard;

/**
 * Cache
 */
public class Cache<TKey, TValue> extends BaseCache<TKey, TValue> {

    /**
     * 缓存大小
     */
    private static final int DEFAULT_CACHE_CAPACITY = 10000;

    private int capacity;

    private final Map<TKey, CacheItem<TValue>> cacheBag;

    private final ReentrantReadWriteLock locker = new ReentrantReadWriteLock();

    public Cache() {
        this(DEFAULT_CACHE_CAPACITY);
    }

    public Cache(int capacity) {
        this.capacity = capacity;
        cacheBag = createCacheBag();
    }

    /**
     * 获取缓存
     * @param key 缓存Key
     * @return
     */
    public TValue get(TKey key) {
        Guard.notNull(key, "the key is required.");

        ReadLock readLock = locker.readLock();
        readLock.lock();

        try {
            CacheItem<TValue> item = cacheBag.get(key);
            if(item == null || isExpired(item)) {
                return null;
            }

            return item.getValue();
        } finally {
            readLock.unlock();
        }
    }

    /**
     * 设置缓存
     * @param key 缓存Key
     * @param value 缓存Value
     */
    public void put(TKey key, TValue value) {
        Guard.notNull(key, "the key is required.");

        WriteLock writeLock = locker.writeLock();
        writeLock.lock();

        try {
            int size = cacheBag.size();
            if(size == capacity) {
                shrink();
            }

            CacheItem<TValue> item = createCacheItem(value);
            cacheBag.put(key, item);

        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 批量设置缓存
     * @param map 要被设置的缓存Map集合
     */
    public void putAll(Map<TKey, TValue> map) {
        if(map == null || map.isEmpty()) {
            return;
        }

        WriteLock writeLock = locker.writeLock();
        writeLock.lock();

        try {
            int size = cacheBag.size();
            if(size == capacity) {
                shrink();
            }

            for(TKey key : map.keySet()) {
                if(key == null) {
                    continue;
                }
                TValue value = map.get(key);
                CacheItem<TValue> item = createCacheItem(value);
                cacheBag.put(key, item);
            }

        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 移除缓存
     * @param key 缓存Key
     * @return
     */
    public boolean remove(TKey key) {
        if(key == null) {
            return false;
        }

        WriteLock writeLock = locker.writeLock();
        writeLock.lock();

        try {
            cacheBag.remove(key);
            return true;

        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 批量移除缓存
     * @param keys 缓存Key集合
     * @return
     */
    public boolean remove(Set<TKey> keys) {
        if(keys == null || keys.isEmpty()) {
            return false;
        }

        WriteLock writeLock = locker.writeLock();
        writeLock.lock();

        try {
            for(TKey key : keys) {
                cacheBag.remove(key);
            }
            return true;

        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 收缩，移除过期的缓存项
     */
    protected boolean shrink() {
        int oldSize = cacheBag.size();
        
        Iterator<Entry<TKey, CacheItem<TValue>>> iterator = cacheBag.entrySet().iterator();
        while(iterator.hasNext()) {
            Entry<TKey, CacheItem<TValue>> entry = iterator.next();
            CacheItem<TValue> item = entry.getValue();
            if(item.isExpired() || isExpired(item)) {
                iterator.remove();
            }
        }

        int newSize = cacheBag.size();

        return newSize < oldSize;
    }

    /**
     * 创建缓存容器，默认为LinkedHashMap，LRU模式
     * @return
     */
    protected Map<TKey, CacheItem<TValue>> createCacheBag() {
        return new CacheBag<TKey, TValue>(capacity);
    }

    /**
     * 缓存容器
     * @param <TKey>
     * @param <TValue>
     */
    protected static class CacheBag<TKey, TValue> extends LinkedHashMap<TKey, CacheItem<TValue>> {

        private static final long serialVersionUID = 1L;

        static final float DEFAULT_LOAD_FACTOR = 0.75F;

        private final int maxEntries;

        public CacheBag(int capacity) {
            // 一次性开辟内存空间，避免中途扩展
            super((int) ((float)capacity / DEFAULT_LOAD_FACTOR + 1.0F), DEFAULT_LOAD_FACTOR, true);
            this.maxEntries = capacity;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<TKey, CacheItem<TValue>> eldest) {
            return size() > maxEntries;
        }
        
    }
}
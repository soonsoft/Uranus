package com.soonsoft.uranus.util.caching;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import com.soonsoft.uranus.core.common.collection.MapUtils;

/**
 * 缓存功能
 */
public abstract class Cacheable<TKey, TValue> implements ICacheable<TKey, TValue> {

    /**
     * 缓存是否启用
     */
    private boolean enabled = true;

    private Cache<TKey, TValue> cache;

    private ICache<TKey, TValue> fastCache;

    public Cacheable() {
        this(16 * 16, 0);
    }

    public Cacheable(int capacity, long expireTime) {
        cache = new Cache<>(capacity);
        cache.setExpireTime(expireTime);
    }

    /**
     * @return the enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @param fastCache the fastCache to set
     */
    public void setFastCache(ICache<TKey, TValue> fastCache) {
        this.fastCache = fastCache;
    }

    protected ICache<TKey, TValue> getCache() {
        return this.cache;
    }

    protected ICache<TKey, TValue> getFastCache() {
        return this.fastCache;
    }

    @Override
    public TValue get(TKey key) {
        if(key == null) {
            return null;
        }

        Set<TKey> keys = new HashSet<>(2);
        keys.add(key);
        Map<TKey, TValue> result = get(keys);
        if(result != null) {
            return result.get(key);
        }
        return null;
    }

    @Override
    public Map<TKey, TValue> get(Set<TKey> keys) {
        if(keys == null || keys.isEmpty()) {
            return null;
        }

        Map<TKey, TValue> result = MapUtils.createHashMap(keys.size());

        // 一级缓存
        CacheResult<TKey, TValue> fastCacheResult = null;
        if(fastCache != null) {
            fastCacheResult = getFromCache(keys, this::getFastCache);
            if(!fastCacheResult.isMissing()) {
                return fastCacheResult.getCacheValues();
            }
            result.putAll(fastCacheResult.getCacheValues());
        }

        // 二级缓存
        CacheResult<TKey, TValue> cacheResult = null;
        if(fastCacheResult != null) {
            cacheResult = getFromCache(fastCacheResult.getMissingKeys(), this::getCache);
        } else {
            cacheResult = getFromCache(keys, this::getCache);
        }
        result.putAll(cacheResult.getCacheValues());
        if(cacheResult.isMissing()) {
            // 加载数据
            Map<TKey, TValue> data = load(cacheResult.getMissingKeys());
            if(data != null) {
                result.putAll(data);
                cache.putAll(data);
            }
        }

        if(fastCacheResult != null) {
            Map<TKey, TValue> cacheData = MapUtils.createHashMap(fastCacheResult.getMissingKeys().size());
            for(TKey key : fastCacheResult.getMissingKeys()) {
                cacheData.put(key, result.get(key));
            }
            fastCache.putAll(cacheData);
        }

        return result;
    }

    private CacheResult<TKey, TValue> getFromCache(Set<TKey> keys, Supplier<ICache<TKey, TValue>> cacheGetter) {
        CacheResult<TKey, TValue> result = new CacheResult<>();
        ICache<TKey, TValue> cache = cacheGetter.get();

        Map<TKey, TValue> values = MapUtils.createHashMap(keys.size());
        Set<TKey> missingKeys = new HashSet<>(Math.max((int) (keys.size()/.75f) + 1, 16));
        for(TKey key : keys) {
            TValue value = cache.get(key);
            if(value == null) {
                missingKeys.add(key);
            } else {
                values.put(key, value);
            }
        }
        result.setCacheValues(values);
        result.setMissingKeys(missingKeys);
        
        return result;
    }

    protected abstract Map<TKey, TValue> load(Set<TKey> keys);

    protected static class CacheResult<TKey, TValue> {

        public Set<TKey> missingKeys;

        public Map<TKey, TValue> cacheValues;

        public Set<TKey> getMissingKeys() {
            return missingKeys;
        }

        public void setMissingKeys(Set<TKey> missingKeys) {
            this.missingKeys = missingKeys;
        }

        public Map<TKey, TValue> getCacheValues() {
            return cacheValues;
        }

        public void setCacheValues(Map<TKey, TValue> cacheValues) {
            this.cacheValues = cacheValues;
        }

        public boolean isMissing() {
            return this.missingKeys == null || this.missingKeys.isEmpty();
        }

    }
    
}
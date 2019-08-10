package com.soonsoft.uranus.web.mvc.caching;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.soonsoft.uranus.util.caching.ICache;
import com.soonsoft.uranus.web.HttpContext;

/**
 * WebThreadCache
 */
public class WebThreadCache<TKey, TValue> implements ICache<TKey, TValue> {

    private static final String THREAD_CACHE_KEY = "WebThreadCacheKey";

    @Override
    public TValue get(TKey key) {
        return getCacheBag().get(key);
    }

    @Override
    public void put(TKey key, TValue value) {
        getCacheBag().put(key, value);
    }

    @Override
    public void putAll(Map<TKey, TValue> map) {
        getCacheBag().putAll(map);
    }

    @Override
    public boolean remove(TKey key) {
        getCacheBag().remove(key);
        return true;
    }

    @Override
    public boolean remove(Set<TKey> keys) {
        Map<TKey, TValue> cacheBag = getCacheBag();
        for(TKey key : keys) {
            cacheBag.remove(key);
        }
        return true;
    }

    private Map<TKey, TValue> getCacheBag() {
        HttpContext context = HttpContext.current();
        Map<TKey, TValue> cacheBag = context.getAttribute(THREAD_CACHE_KEY);
        if(cacheBag == null) {
            cacheBag = new HashMap<TKey, TValue>();
            context.setAttribute(THREAD_CACHE_KEY, cacheBag);
        }
        return cacheBag;
    }
    
}
package com.soonsoft.uranus.util.caching;

/**
 * BaseCache
 */
public abstract class BaseCache<TKey, TValue> implements ICache<TKey, TValue> {

    /**
     * 过期时间，单位毫秒
     */
    private long expireTime = 0;

    /**
     * @return the expireTime
     */
    public long getExpireTime() {
        return expireTime;
    }

    /**
     * 设置过期时间，单位毫秒，默认值为0，表示不过期
     * @param expireTime the expireTime to set
     */
    public void setExpireTime(long expireTime) {
        if(expireTime < 0) {
            this.expireTime = 0;
        } else {
            this.expireTime = expireTime;
        }
    }

    /**
     * 判断缓存是否过期
     * 如果没有设置expireTime，则表示永不过期
     * @param item
     * @return
     */
    protected boolean isExpired(CacheItem<TValue> item) {
        if(expireTime == 0) {
            return false;
        }
        long cacheTime = item.getCacheTime();
        if(cacheTime == 0) {
            return false;
        }
        if(item.isExpired()) {
            return true;
        }
        long currentTime = System.currentTimeMillis();
        item.setExpired((currentTime - cacheTime) > expireTime);
        return item.isExpired();
    }

    protected CacheItem<TValue> createCacheItem(TValue value) {
        if(expireTime == 0) {
            return new CacheItem<>(value);
        } else {
            return new CacheItem<>(value, System.currentTimeMillis());
        }
    }

    /**
     * 缓存项目
     * @param <TValue>
     */
    protected static class CacheItem<TValue> {
        private boolean expired = false;
        private long cacheTime = 0;
        private TValue value;

        public CacheItem(TValue value) {
            this.value = value;
        }

        public CacheItem(TValue value, long cacheTime) {
            this.value = value;
            this.cacheTime = cacheTime;
        }

        public void setExpired(boolean expired) {
            this.expired = expired;
        }

        public boolean isExpired() {
            return expired;
        }

        public TValue getValue() {
            return value;
        }

        public long getCacheTime() {
            return cacheTime;
        }
    }
    
}
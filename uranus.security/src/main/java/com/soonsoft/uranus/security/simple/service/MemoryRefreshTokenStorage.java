package com.soonsoft.uranus.security.simple.service;

import com.soonsoft.uranus.security.config.api.ITokenStorage;
import com.soonsoft.uranus.util.caching.Cache;

public class MemoryRefreshTokenStorage implements ITokenStorage {

    private Cache<String, String> refreshTokenStore;

    public MemoryRefreshTokenStorage(int minutes) {
        refreshTokenStore = new Cache<>(1000);
        refreshTokenStore.setExpireTime(minutes * 60 * 1000);
    }

    @Override
    public boolean contains(String key, String token) {
        String value = refreshTokenStore.get(key);
        return value != null && value.equals(token);
    }

    @Override
    public void remove(String key) {
        refreshTokenStore.remove(key);
    }

    @Override
    public void set(String key, String token) {
        refreshTokenStore.put(key, token);
    }

    @Override
    public String get(String key) {
        return refreshTokenStore.get(key);
    }
    
}

package com.soonsoft.uranus.services.membership.service;

import com.soonsoft.uranus.security.authentication.ITokenStorage;

// TODO 实现基于DB的RefreshToken的存储器
public class DBRefreshTokenStorage implements ITokenStorage {

    @Override
    public boolean contains(String key, String token) {
        return false;
    }

    @Override
    public void remove(String key) {
        
    }

    @Override
    public void set(String key, String token) {
        
    }

    @Override
    public String get(String key) {
        return null;
    }
    
}

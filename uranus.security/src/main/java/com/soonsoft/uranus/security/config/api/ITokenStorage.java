package com.soonsoft.uranus.security.config.api;

public interface ITokenStorage {

    boolean contains(String key, String token);

    void remove(String key);

    void set(String key, String token);

    String get(String key);
    
}

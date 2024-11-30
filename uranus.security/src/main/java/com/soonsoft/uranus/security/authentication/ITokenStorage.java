package com.soonsoft.uranus.security.authentication;

public interface ITokenStorage {

    boolean contains(String key, String token);

    void remove(String key);

    void set(String key, String token);

    String get(String key);
    
}

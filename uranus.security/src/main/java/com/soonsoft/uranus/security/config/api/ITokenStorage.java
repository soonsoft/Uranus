package com.soonsoft.uranus.security.config.api;

public interface ITokenStorage {

    boolean contains(String token);

    void remove(String token);

    void put(String token);
    
}

package com.soonsoft.uranus.security.jwt;

public interface ITokenStrategy {
    
    IApiToken createToken(String username, String password);

}

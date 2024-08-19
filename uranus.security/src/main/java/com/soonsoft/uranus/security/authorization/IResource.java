package com.soonsoft.uranus.security.authorization;

public interface IResource<T> {

    T getResourceCode();

    String getResourceUrl();
    
}
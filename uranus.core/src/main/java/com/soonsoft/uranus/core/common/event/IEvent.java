package com.soonsoft.uranus.core.common.event;

/**
 * 事件对象
 */
public interface IEvent<T> {

    T getData();

    String getName();
    
}
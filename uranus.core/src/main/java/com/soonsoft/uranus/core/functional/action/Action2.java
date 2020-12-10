package com.soonsoft.uranus.core.functional.action;

@FunctionalInterface
public interface Action2<P1, P2> {

    void apply(P1 param1, P2 param2);
    
}

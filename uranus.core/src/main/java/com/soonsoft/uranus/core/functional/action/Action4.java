package com.soonsoft.uranus.core.functional.action;

@FunctionalInterface
public interface Action4<P1, P2, P3, P4> {

    void apply(P1 param1, P2 param2, P3 param3, P4 param4);
    
}

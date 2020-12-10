package com.soonsoft.uranus.core.functional.action;

@FunctionalInterface
public interface Action3<P1, P2, P3> {

    void apply(P1 param1, P2 param2, P3 param3);
    
}

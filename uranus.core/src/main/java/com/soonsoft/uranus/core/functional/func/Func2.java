package com.soonsoft.uranus.core.functional.func;

@FunctionalInterface
public interface Func2<P1, P2, R> {
    
    R call(P1 param1, P2 param2);

}

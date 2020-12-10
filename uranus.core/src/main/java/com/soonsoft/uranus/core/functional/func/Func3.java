package com.soonsoft.uranus.core.functional.func;

@FunctionalInterface
public interface Func3<P1, P2, P3, R> {
    
    R call(P1 param1, P2 param2, P3 param3);

}

package com.soonsoft.uranus.core.functional.func;

import java.io.Serializable;

@FunctionalInterface
public interface Func4<P1, P2, P3, P4, R> extends Serializable {
    
    R call(P1 param1, P2 param2, P3 param3, P4 param4);

}

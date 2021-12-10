package com.soonsoft.uranus.core.functional.func;

import java.io.Serializable;

@FunctionalInterface
public interface Func6<P1, P2, P3, P4, P5, P6, R> extends Serializable {
    
    R call(P1 param1, P2 param2, P3 param3, P4 param4, P5 param5, P6 param6);

}

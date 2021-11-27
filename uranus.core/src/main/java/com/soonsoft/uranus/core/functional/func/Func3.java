package com.soonsoft.uranus.core.functional.func;

import java.io.Serializable;

@FunctionalInterface
public interface Func3<P1, P2, P3, R> extends Serializable {
    
    R call(P1 param1, P2 param2, P3 param3);

}

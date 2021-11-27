package com.soonsoft.uranus.core.functional.func;

import java.io.Serializable;

@FunctionalInterface
public interface Func2<P1, P2, R> extends Serializable {
    
    R call(P1 param1, P2 param2);

}

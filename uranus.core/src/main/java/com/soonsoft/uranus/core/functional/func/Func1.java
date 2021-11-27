package com.soonsoft.uranus.core.functional.func;

import java.io.Serializable;

@FunctionalInterface
public interface Func1<P, R> extends Serializable {
    
    R call(P param);

}

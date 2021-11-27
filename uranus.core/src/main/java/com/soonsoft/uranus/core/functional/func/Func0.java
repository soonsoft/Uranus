package com.soonsoft.uranus.core.functional.func;

import java.io.Serializable;

@FunctionalInterface
public interface Func0<R> extends Serializable {
    
    R call();

}

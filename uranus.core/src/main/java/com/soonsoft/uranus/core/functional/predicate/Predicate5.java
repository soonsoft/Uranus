package com.soonsoft.uranus.core.functional.predicate;

import java.io.Serializable;

@FunctionalInterface
public interface Predicate5<P1, P2, P3, P4, P5> extends Serializable {
    
    boolean test(P1 param1, P2 param2, P3 param3, P4 param4, P5 param5);

}

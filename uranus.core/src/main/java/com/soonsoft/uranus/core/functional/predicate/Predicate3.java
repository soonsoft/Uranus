package com.soonsoft.uranus.core.functional.predicate;

import java.io.Serializable;

@FunctionalInterface
public interface Predicate3<P1, P2, P3> extends Serializable {
    
    boolean test(P1 param1, P2 param2, P3 param3);

}

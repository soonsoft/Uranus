package com.soonsoft.uranus.core.functional.predicate;

import java.io.Serializable;

@FunctionalInterface
public interface Predicate2<P1, P2> extends Serializable {
    
    boolean test(P1 param1, P2 param2);

}

package com.soonsoft.uranus.core.functional.predicate;

import java.io.Serializable;

@FunctionalInterface
public interface Predicate1<P> extends Serializable {
    
    boolean test(P param);

}

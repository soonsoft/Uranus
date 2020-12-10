package com.soonsoft.uranus.core.functional.predicate;

@FunctionalInterface
public interface Predicate2<P1, P2> {
    
    boolean test(P1 param1, P2 param2);

}

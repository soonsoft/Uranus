package com.soonsoft.uranus.core.functional.predicate;

import java.io.Serializable;

@FunctionalInterface
public interface Predicate9<P1, P2, P3, P4, P5, P6, P7, P8, P9> extends Serializable {
    
    boolean test(P1 param1, P2 param2, P3 param3, P4 param4, P5 param5, P6 param6, P7 param7, P8 param8, P9 param9);

}

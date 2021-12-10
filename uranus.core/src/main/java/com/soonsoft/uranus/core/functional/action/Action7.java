package com.soonsoft.uranus.core.functional.action;

import java.io.Serializable;

@FunctionalInterface
public interface Action7<P1, P2, P3, P4, P5, P6, P7> extends Serializable {

    void apply(P1 param1, P2 param2, P3 param3, P4 param4, P5 param5, P6 param6, P7 param7);
    
}

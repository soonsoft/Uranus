package com.soonsoft.uranus.core.functional.action;

import java.io.Serializable;

@FunctionalInterface
public interface Action2<P1, P2> extends Serializable {

    void apply(P1 param1, P2 param2);
    
}

package com.soonsoft.uranus.core.functional.action;

import java.io.Serializable;

@FunctionalInterface
public interface Action1<P> extends Serializable {

    void apply(P param);
    
}

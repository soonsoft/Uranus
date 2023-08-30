package com.soonsoft.uranus.core.functional.behavior;

import com.soonsoft.uranus.core.functional.action.Action1;

public interface IDeepEach<TElement> {

    void deepEach(Action1<TElement> action);
    
}

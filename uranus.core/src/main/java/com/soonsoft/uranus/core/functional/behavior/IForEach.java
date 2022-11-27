package com.soonsoft.uranus.core.functional.behavior;

import com.soonsoft.uranus.core.functional.action.Action3;

public interface IForEach<TElement> {
    
    void forEach(Action3<TElement, Integer, ForEachBehavior> action);

}

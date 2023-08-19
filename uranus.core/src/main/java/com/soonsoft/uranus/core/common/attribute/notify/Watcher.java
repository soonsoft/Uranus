package com.soonsoft.uranus.core.common.attribute.notify;


import com.soonsoft.uranus.core.functional.func.Func1;

public class Watcher<TAccessor, TValue> {

    private TAccessor accessor;
    private Func1<TAccessor, TValue> computedAction;
    private Dependency<?> dependency;
    
    public TValue getComputedValue() {
        IUpdateDelegate a = null;

        dependency.setCurrent(a);
        try {
            return computedAction.call(accessor);
        } finally {
            dependency.setCurrent(null);
        }
    }

}

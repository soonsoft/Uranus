package com.soonsoft.uranus.core.common.attribute.notify;

import com.soonsoft.uranus.core.functional.func.Func1;

public class ComputedWatcher<TAccessor, TValue> extends Watcher<TValue> {
    
    private TAccessor accessor;
    private Func1<TAccessor, TValue> computedFn;

    public ComputedWatcher(TAccessor accessor, Dependency<?> dependency, Func1<TAccessor, TValue> computedFn) {
        super(dependency);
        this.accessor = accessor;
        this.computedFn = computedFn;
    }

    @Override
    protected TValue computeValue() {
        dependency.setCurrent(updateDelegate);
        try {
            return computedFn.call(accessor);
        } finally {
            dependency.setCurrent(null);
        }
    }
    
    
}

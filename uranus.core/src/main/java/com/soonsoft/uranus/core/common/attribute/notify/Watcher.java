package com.soonsoft.uranus.core.common.attribute.notify;


import com.soonsoft.uranus.core.functional.action.Action1;
import com.soonsoft.uranus.core.functional.func.Func0;
import com.soonsoft.uranus.core.functional.func.Func1;

public class Watcher<TAccessor, TValue> {

    private TAccessor accessor;
    private Dependency<?> dependency;
    private Func1<TAccessor, TValue> computedFn;
    private Action1<TValue> updateAction;
    private final IUpdateDelegate updateDelegate;
    private TValue value;
    private Func0<TValue> valueGetter;

    public Watcher(TAccessor accessor, Dependency<?> dependency, Func1<TAccessor, TValue> computedFn) {
        this.accessor = accessor;
        this.dependency = dependency;
        this.computedFn = computedFn;

        updateDelegate = new IUpdateDelegate() {
            @Override
            public void update() {
                value = computeValue();
                if(updateAction != null) {
                    updateAction.apply(value);
                }
            }
        };
    }

    public void setUpdateAction(Action1<TValue> updateAction) {
        this.updateAction = updateAction;
    }
    
    public TValue getComputedValue() {
        if(valueGetter != null) {
            return valueGetter.call();
        }
        this.value = computeValue();
        valueGetter = () -> this.value;
        return this.value;
    }

    private TValue computeValue() {
        dependency.setCurrent(updateDelegate);
        try {
            return computedFn.call(accessor);
        } finally {
            dependency.setCurrent(null);
        }
    }

}

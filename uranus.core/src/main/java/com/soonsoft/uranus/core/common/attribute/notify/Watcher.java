package com.soonsoft.uranus.core.common.attribute.notify;

import com.soonsoft.uranus.core.functional.action.Action1;
import com.soonsoft.uranus.core.functional.func.Func0;

public class Watcher<TValue> {

    protected Dependency<?> dependency;
    protected final IUpdateDelegate updateDelegate;
    // 计算逻辑
    protected Func0<TValue> computedFn;
    // 缓存计算结果
    private TValue value;
    private Action1<TValue> updateAction;
    private Func0<TValue> valueGetter;

    protected Watcher(Dependency<?> dependency) {
        this.dependency = dependency;

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

    public Watcher(Dependency<?> dependency, Func0<TValue> computedFn) {
        this(dependency);
        this.computedFn = computedFn;
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

    protected TValue computeValue() {
        dependency.setCurrent(updateDelegate);
        try {
            return computedFn.call();
        } finally {
            dependency.setCurrent(null);
        }
    }

}

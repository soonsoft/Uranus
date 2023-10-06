package com.soonsoft.uranus.core.common.attribute;

import java.util.ArrayList;
import java.util.List;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.common.attribute.access.AttributeBag;
import com.soonsoft.uranus.core.common.attribute.data.AttributeData;
import com.soonsoft.uranus.core.common.attribute.notify.Dependency;
import com.soonsoft.uranus.core.common.attribute.notify.Watcher;
import com.soonsoft.uranus.core.functional.action.Action1;
import com.soonsoft.uranus.core.functional.func.Func0;
import com.soonsoft.uranus.core.functional.func.Func1;

public class AttributeBagFactory {

    private Dependency<String> dependency;

    public AttributeBagFactory() {
        this(new Dependency<>());
    }

    public AttributeBagFactory(Dependency<String> dependency) {
        this.dependency = dependency;
    }

    public IAttributeBag createBag() {
        return createBag(new ArrayList<>());
    }

    public IAttributeBag createBag(List<AttributeData> attributeDataList) {
        return new AttributeBag(attributeDataList, dependency);
    }

    public IAttributeBag createBag(List<AttributeData> attributeDataList, Func1<AttributeData, ComputedAttribute<?>> computedAttributeFinder) {
        return new AttributeBag(attributeDataList, dependency, computedAttributeFinder);
    }

    public <TValue> Watcher<TValue> watch(Func0<TValue> computedFn) {
        return watch(computedFn, null);
    }

    public <TValue> Watcher<TValue> watch(Func0<TValue> computedFn, Action1<TValue> updateAction) {
        Guard.notNull(computedFn, "the arguments action is required.");

        Watcher<TValue> watcher = new Watcher<>(dependency, computedFn);
        watcher.setUpdateAction(updateAction);

        return watcher;
    }
    
}

package com.soonsoft.uranus.core.common.attribute.access;

import com.soonsoft.uranus.core.common.attribute.notify.IUpdateDelegate;
import com.soonsoft.uranus.core.functional.action.Action1;

public class DefaultUpdateDelegate implements IUpdateDelegate {

    private final StructDataAccessor object;
    private final Action1<StructDataAccessor> updateAction;

    public DefaultUpdateDelegate(StructDataAccessor object, Action1<StructDataAccessor> updateAction) {
        this.object = object;
        this.updateAction = updateAction;
    }

    @Override
    public void update() {
        updateAction.apply(object);
    }
    
}

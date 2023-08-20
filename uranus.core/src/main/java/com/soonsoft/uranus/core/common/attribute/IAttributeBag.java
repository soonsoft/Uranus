package com.soonsoft.uranus.core.common.attribute;

import com.soonsoft.uranus.core.common.attribute.access.ActionCommand;
import com.soonsoft.uranus.core.common.attribute.access.StructDataAccessor;
import com.soonsoft.uranus.core.functional.action.Action1;

public interface IAttributeBag {
    
    StructDataAccessor getEntity();

    StructDataAccessor getEntity(String entityName);
    
    StructDataAccessor getEntityOrNew(String entityName);

    boolean hasEntity(String entityName);

    void saveChanges(Action1<ActionCommand> action);

    int getActionCommandCount();

}

package com.soonsoft.uranus.core.common.attribute;

import com.soonsoft.uranus.core.common.attribute.access.ActionCommand;
import com.soonsoft.uranus.core.common.attribute.access.StructDataAccessor;
import com.soonsoft.uranus.core.functional.action.Action1;

public interface IAttributeBag {
    
    StructDataAccessor getEntity();

    StructDataAccessor getEntity(String entityName);

    default StructDataAccessor getEntityOrNew(String entityName) {
        return getEntityOrNew(entityName, entityName);
    }
    
    StructDataAccessor getEntityOrNew(String entityName, String dataId);

    boolean hasEntity(String entityName);

    void saveChanges(Action1<ActionCommand> action);

    int getActionCommandCount();

    IAttributeJsonTemplate toJSON();

}

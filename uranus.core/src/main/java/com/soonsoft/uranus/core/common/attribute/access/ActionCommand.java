package com.soonsoft.uranus.core.common.attribute.access;

import com.soonsoft.uranus.core.Guard;
import com.soonsoft.uranus.core.common.attribute.data.AttributeData;
import com.soonsoft.uranus.core.common.lang.StringUtils;

public class ActionCommand {
    private final ActionType actionType;
    private final AttributeData attributeData;

    public ActionCommand(ActionType type, AttributeData data) {
        Guard.notNull(data, "the arguments data is required.");
        this.actionType = type;
        this.attributeData = data;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public AttributeData getAttributeData() {
        return attributeData;
    }

    @Override
    public int hashCode() {
        return attributeData.getKey().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ActionCommand other = (ActionCommand) obj;
        String thisKey = attributeData.getKey();
        String otherKey = other.attributeData.getKey();

        return StringUtils.equals(thisKey, otherKey);
    }

    
}

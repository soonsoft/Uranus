package com.soonsoft.uranus.core.common.attribute.data;

import java.util.List;

import com.soonsoft.uranus.core.functional.action.Action3;
import com.soonsoft.uranus.core.functional.behavior.ForEachBehavior;
import com.soonsoft.uranus.core.functional.behavior.IForEach;

public class AttributeArrayData extends AttributeData implements IForEach<AttributeData> {

    private List<AttributeData> elements;

    Object getValue() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAttributeValue'");
    }

    public Object getAttributeValue(int index) {
        return getValue();
    }

    @Override
    public void forEach(Action3<AttributeData, Integer, ForEachBehavior> action) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'forEach'");
    }
    
}

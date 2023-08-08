package com.soonsoft.uranus.core.common.attribute.access;

import com.soonsoft.uranus.core.common.attribute.Attribute;
import com.soonsoft.uranus.core.common.attribute.access.IndexNode.ListNode;
import com.soonsoft.uranus.core.common.attribute.data.AttributeData;
import com.soonsoft.uranus.core.functional.func.Func1;

public class ArrayDataAccessor extends BaseAccessor {

    public ArrayDataAccessor(ListNode node, Func1<Integer, AttributeData> attributeDataGetter, Func1<AttributeData, Integer> attributeDataAdder) {
        super(node, attributeDataGetter, attributeDataAdder);
    }

    public <TValue> TValue getValue(int index, Attribute<TValue> attribute) {
        checkIndex(index);
        checkAttribute(attribute);

        AttributeData attributeData = getAttributeData(String.valueOf(index), node, attributeDataGetter);
        return attributeData != null ? attribute.convertValue(attributeData.getPropertyValue()) : null;
    }

    public <TValue> void setValue(TValue value, int index, Attribute<TValue> attribute) {
        checkIndex(index);
        checkAttribute(attribute);

        AttributeData attributeData = getAttributeData(String.valueOf(index), node, attributeDataGetter);
        if(attributeData != null) {
            // update
        } else {
            addAttributeData(value, attribute, node, attributeDataGetter, attributeDataAdder);
        }
    }

    private void checkIndex(int index) {
        if(index < 0 || index >= node.getChildren().size()) {
            throw new IndexOutOfBoundsException(index);
        }
    }
    
}

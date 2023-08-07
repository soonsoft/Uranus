package com.soonsoft.uranus.core.common.attribute;

import com.soonsoft.uranus.core.common.attribute.access.IAttributeAccessor;
import com.soonsoft.uranus.core.common.attribute.data.AttributeData;

public interface IAttributeBag<TValue> extends IAttributeAccessor<TValue> {

    AttributeData getAttributeData(String attributeCode);

    void setAttributeData(AttributeData attributeData);

    boolean removeAttribute();

}

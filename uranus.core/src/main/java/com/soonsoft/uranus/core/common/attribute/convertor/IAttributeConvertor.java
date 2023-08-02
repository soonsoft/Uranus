package com.soonsoft.uranus.core.common.attribute.convertor;

public interface IAttributeConvertor<TValue> {
    
    TValue convert(String attributeValue);

}

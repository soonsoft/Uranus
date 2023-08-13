package com.soonsoft.uranus.core.common.attribute.convertor;

public interface IAttributeConvertor<TValue> {
    
    default TValue convert(String attributeValue) {
        return convert(attributeValue, null);
    }

    TValue convert(String attributeValue, String defaultValue);

    default String toStringValue(TValue value) {
        return value == null ? null : String.valueOf(value);
    }

}

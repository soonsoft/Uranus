package com.soonsoft.uranus.core.common.attribute.data;

import com.soonsoft.uranus.core.common.attribute.convertor.IAttributeConvertor;

public class AttributeSingleData extends AttributeData {

    private String attributeValue;
    private String attributeEncryptValue;
    private IAttributeConvertor<?> convertor;

    public Object getValue() {
        return convertor != null ? convertor.convert(attributeValue) : null;
    }

    public String getPropertyValue() {
        return this.attributeValue;
    }
    public void setPropertyValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    public String getAttributeEncryptValue() {
        return attributeEncryptValue;
    }
    public void setAttributeEncryptValue(String attributeEncryptValue) {
        this.attributeEncryptValue = attributeEncryptValue;
    }
    
}

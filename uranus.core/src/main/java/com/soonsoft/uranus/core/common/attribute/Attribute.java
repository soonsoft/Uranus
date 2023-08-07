package com.soonsoft.uranus.core.common.attribute;

import com.soonsoft.uranus.core.common.attribute.convertor.IAttributeConvertor;

/**
 * 属性定义
 */
public class Attribute<TValue> {
    
    private final String entityName;
    private final String attributeCode;
    private String defaultValue;

    private final IAttributeConvertor<TValue> valueConvertor;

    public Attribute(String entityName, String attributeCode, IAttributeConvertor<TValue> valueConvertor) {
        this.entityName = entityName;
        this.attributeCode = attributeCode;
        this.valueConvertor = valueConvertor;
    }

    public IAttributeConvertor<TValue> getConvertor() {
        return this.valueConvertor;
    }

    public TValue convertValue(String attributeValue) {
        return this.valueConvertor != null 
                ? this.valueConvertor.convert(attributeValue, defaultValue) 
                : null;
    }

    public String getEntityName() {
        return entityName;
    }
    

    public String getAttributeCode() {
        return attributeCode;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

}

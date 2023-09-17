package com.soonsoft.uranus.core.common.attribute;

import com.soonsoft.uranus.core.common.attribute.convertor.IAttributeConvertor;
import com.soonsoft.uranus.core.common.attribute.data.PropertyType;

/**
 * 属性定义
 */
public class Attribute<TValue> {
    // 实体名称
    private final String entityName;
    // 属性名称
    private final String propertyName;
    // 属性的类型
    private PropertyType type = PropertyType.Property;
    // 属性默认值
    private String defaultValue;
    // 属性值转换器
    private final IAttributeConvertor<TValue> valueConvertor;

    public Attribute(String entityName, String propertyName, PropertyType type) {
        this(entityName, propertyName, type, null);
    } 

    public Attribute(String entityName, String propertyName, IAttributeConvertor<TValue> valueConvertor) {
        this.entityName = entityName;
        this.propertyName = propertyName;
        this.valueConvertor = valueConvertor;
    }

    public Attribute(String entityName, String propertyName, PropertyType type, IAttributeConvertor<TValue> valueConvertor) {
        this(entityName, propertyName, valueConvertor);
        this.type = type;
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
    
    public String getPropertyName() {
        return propertyName;
    }

    public PropertyType getType() {
        return type;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

}

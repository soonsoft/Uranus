package com.soonsoft.uranus.core.common.attribute;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.soonsoft.uranus.core.common.attribute.data.AttributeData;
import com.soonsoft.uranus.core.common.attribute.data.AttributeKey;
import com.soonsoft.uranus.core.common.attribute.data.DataStatus;
import com.soonsoft.uranus.core.common.attribute.data.PropertyType;

public class AttributeDataBuilder {

    private String dataId;
    private List<AttributeData> data;
    private AttributeKey attributeKey;
    private AttributeDataBuilder parent;
    private String entityName = null;
    private String parentKey = null;

    public AttributeDataBuilder(String dataId, String entityName, List<AttributeData> data, AttributeKey key, AttributeDataBuilder parent) {
        this.dataId = dataId;
        this.data = data;
        this.attributeKey = key;
        this.parent = parent;
        this.entityName = entityName;
    }

    public AttributeDataBuilder done() {
        return this.parent;
    }

    public AttributeDataBuilder entity(String entityName) {
        return new AttributeDataBuilder(dataId, entityName, data, attributeKey, this);
    }

    public AttributeDataBuilder property(String propertyName, String value) {
        AttributeData elem = new AttributeData();
        elem.setDataId(this.dataId);
        elem.setKey(attributeKey.generate());
        elem.setParentKey(this.parentKey);
        elem.setPropertyType(PropertyType.Property);
        elem.setEntityName(this.entityName);
        elem.setPropertyName(propertyName);
        elem.setPropertyValue(value);
        elem.setStatus(DataStatus.Enabled);
        data.add(elem);

        return this;
    }

    public AttributeDataBuilder struct(String entityName, String propertyName) {
        AttributeData elem = new AttributeData();
        elem.setDataId(this.dataId);
        elem.setKey(attributeKey.generate());
        elem.setParentKey(this.entityName); // 一级entity的结构属性，parentKey必须是entityName
        elem.setPropertyType(PropertyType.Struct);
        elem.setEntityName(entityName);
        elem.setPropertyName(propertyName);
        elem.setStatus(DataStatus.Enabled);
        data.add(elem);

        AttributeDataBuilder builder = new AttributeDataBuilder(dataId, entityName, data, attributeKey, this);
        builder.parentKey = elem.getKey();
        return builder;
    }

    public AttributeDataBuilder array(String propertyName, String... values) {
        for(int i = 0; i < values.length; i++) {
            this.property(propertyName, values[i]);
        }
        return this;
    }

    public List<AttributeData> getData() {
        return this.data;
    }

    public static AttributeDataBuilder create() {
        return new AttributeDataBuilder(UUID.randomUUID().toString(), null, new ArrayList<>(100), new AttributeKey(), null);
    }
}

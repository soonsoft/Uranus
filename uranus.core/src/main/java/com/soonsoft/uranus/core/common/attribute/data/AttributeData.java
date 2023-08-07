package com.soonsoft.uranus.core.common.attribute.data;

/**
 * 属性信息<br>
 * 属性解析规则<br>
 *   1. 一个属性代表一个 Property<br>
 *   2. 如果两个属性的 entityName 相同，则代表这两个属性同属于一个实体<br>
 *   3. 如果两个属性的 entityName 相同并且 propertyName 也相同，则代表他们是同属于一个集合（Array 结构）<br>
 *   4. 默认的 propertyType 为 [Property]，代表是属性，为 [Struct] 则表示这个属性为“结构体”，结构体没有 Value，用于承载子对象（Object 结构： { Struct: {} }）<br>
 *   5. EntityName 用于描述属性所属的实体对象定义<br>
 *   6. 对于特殊的 CommonEntity，会用 propertyName 辅助表达，如：{ entityName: Customer, propertyName: AddressEntity::addressDetail }<br>
 */
public class AttributeData {

    private String id;
    private String dataId;
    private String entityName;
    private String propertyName;
    private PropertyType propertyType;
    private String parentId;
    private String attributeName;
    private String attributeValue;
    private String attributeEncryptValue;
    private String status;
    private String remark;


    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    
    public String getDataId() {
        return dataId;
    }
    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getEntityName() {
        return entityName;
    }
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }
    
    public String getPropertyName() {
        return propertyName;
    }
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }
    
    public PropertyType getPropertyType() {
        return propertyType;
    }
    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
    }

    public String getParentId() {
        return parentId;
    }
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getAttributeName() {
        return attributeName;
    }
    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeValue() {
        return attributeValue;
    }
    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    public String getAttributeEncryptValue() {
        return attributeEncryptValue;
    }
    public void setAttributeEncryptValue(String attributeEncryptValue) {
        this.attributeEncryptValue = attributeEncryptValue;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }

}

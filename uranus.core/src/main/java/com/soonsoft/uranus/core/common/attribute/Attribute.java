package com.soonsoft.uranus.core.common.attribute;

/**
 * 属性信息<br>
 * 属性解析规则<br>
 *   1. 一个属性代表一个 Property<br>
 *   2. 如果两个属性的 entityId 相同，则代表这两个属性同属于一个实体<br>
 *   3. 如果两个属性的 entityId 相同并且 attributeCode 也相同，则代表他们是同属于一个集合（Array 结构）<br>
 *   4. 默认的 attributeType 为 [Property]，代表是属性，为 [Struct] 则表示这个属性为“结构体”，结构体没有 Value，用于承载子对象（Object 结构： { Struct: {} }）<br>
 *   5. EntityName 用于描述属性所属的实体对象定义<br>
 *   6. 对于特殊的 CommonEntity，会用 attributeCode 辅助表达，如：{ entityName: Customer, attributeCode: AddressEntity::addressDetail }<br>
 */
public class Attribute {
    
    private String entityId;
    private String entityName;
    private String attributeCode;
    private String attributeType;
    private String parentAttributeCode;
    private String attributeName;
    private String attributeValue;
    private String attributeEncryptValue;
    private String status;
    private String remark;

}

package com.soonsoft.uranus.core.common.access;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.soonsoft.uranus.core.common.attribute.Attribute;
import com.soonsoft.uranus.core.common.attribute.access.AttributeBag;
import com.soonsoft.uranus.core.common.attribute.access.StructDataAccessor;
import com.soonsoft.uranus.core.common.attribute.convertor.AttributeDataType;
import com.soonsoft.uranus.core.common.attribute.data.AttributeData;
import com.soonsoft.uranus.core.common.attribute.data.PropertyType;

public class AttributeBagTest {

    @Test
    public void test_ArrayValue() {
        AttributeBag bag = new AttributeBag();
        StructDataAccessor person = bag.newEntity("Person");
        Attribute<String> nameProperty = new Attribute<String>("Person", "Name", AttributeDataType.StringConvetor);
        person.setValue("Jack", nameProperty);

        Assert.assertTrue("Jack".equals(person.getValue(nameProperty)));
    }
    
    @Test
    public void test_createBag() {

        List<AttributeData> list = new ArrayList<>();
        addData(list, "Individual", "Name", null, PropertyType.PROPERTY);
        addData(list, "Individual", "EnName", null, PropertyType.PROPERTY);
        addData(list, "Individual", "LastName", null, PropertyType.PROPERTY);
        addData(list, "Individual", "FirstName", null, PropertyType.PROPERTY);
        addData(list, "Individual", "Email", null, PropertyType.PROPERTY);
        addData(list, "Individual", "Email", null, PropertyType.PROPERTY);
        addData(list, "Individual", "Address", null, PropertyType.STRUCT);
        addData(list, "Individual", "AddressDetail", "Address", PropertyType.PROPERTY);
        addData(list, "Individual", "AddressType", "Address", PropertyType.PROPERTY);

        addData(list, "Bankcard", "BankAccountNo", null, PropertyType.PROPERTY);
        addData(list, "Bankcard", "SwiftCode", null, PropertyType.PROPERTY);
        addData(list, "Bankcard", "BankAccountName", null, PropertyType.PROPERTY);

        AttributeBag bag = new AttributeBag(list);
        System.out.println(bag);


    }

    private static void addData(List<AttributeData> list, String entityName, String attrCode, String parentAttrCode, PropertyType type) {
        AttributeData attrData = new AttributeData();
        attrData.setEntityName(entityName);
        attrData.setPropertyName(attrCode);
        attrData.setParentKey(parentAttrCode);
        attrData.setPropertyType(type);

        list.add(attrData);
    }

}

package com.soonsoft.uranus.core.common.access;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.soonsoft.uranus.core.common.attribute.Attribute;
import com.soonsoft.uranus.core.common.attribute.access.ActionType;
import com.soonsoft.uranus.core.common.attribute.access.ArrayDataAccessor;
import com.soonsoft.uranus.core.common.attribute.access.AttributeBag;
import com.soonsoft.uranus.core.common.attribute.access.StructDataAccessor;
import com.soonsoft.uranus.core.common.attribute.convertor.AttributeDataType;
import com.soonsoft.uranus.core.common.attribute.convertor.IAttributeConvertor;
import com.soonsoft.uranus.core.common.attribute.data.AttributeData;
import com.soonsoft.uranus.core.common.attribute.data.DataStatus;
import com.soonsoft.uranus.core.common.attribute.data.PropertyType;

public class AttributeBagTest {

    private static abstract class Person {
        private static <TValue> Attribute<TValue> define(String propertyName, IAttributeConvertor<TValue> convertor) {
            return new Attribute<>("Person", propertyName, convertor);
        }
        public final static Attribute<String> Name = define("Name", AttributeDataType.StringConvetor);
        public final static Attribute<String> CellPhoneNumber = define("CellPhoneNumber", AttributeDataType.StringConvetor);
        public final static Attribute<Object> BothAddress = new Attribute<Object>("BothAddress", "BothAddress", PropertyType.STRUCT);
    }

    private static abstract class Address {
        private static <TValue> Attribute<TValue> define(String propertyName, IAttributeConvertor<TValue> convertor) {
            return new Attribute<>("Address", propertyName, convertor);
        }
        public final static Attribute<String> Province = define("Province", AttributeDataType.StringConvetor);
        public final static Attribute<String> City = define("City", AttributeDataType.StringConvetor);
        public final static Attribute<String> District = define("District", AttributeDataType.StringConvetor);
        public final static Attribute<String> Detail = define("Detail", AttributeDataType.StringConvetor);
    }

    @Test
    public void test_EmptyBag() {
        AttributeBag bag = new AttributeBag();
        StructDataAccessor person = bag.newEntity("Person");
        
        person.setValue("Jack", Person.Name);
        Assert.assertTrue("Jack".equals(person.getValue(Person.Name)));

        ArrayDataAccessor array = person.newArray("CellPhoneNumber");
        array.addValue("138-0920-8909", Person.CellPhoneNumber);
        array.addValue("186-7689-1234", Person.CellPhoneNumber);

        Assert.assertTrue(person.getArray(Person.CellPhoneNumber).size() == 2);
        Assert.assertTrue(person.getArray(Person.CellPhoneNumber).getValue(0, Person.CellPhoneNumber).equals("138-0920-8909"));
        Assert.assertTrue(person.getArray(Person.CellPhoneNumber).getValue(1, Person.CellPhoneNumber).equals("186-7689-1234"));

        StructDataAccessor bothAddress = person.newStruct("Address", "BothAddress");
        bothAddress.setValue("江苏省", Address.Province);
        bothAddress.setValue("南京市", Address.City);
        bothAddress.setValue("江宁区", Address.District);
        bothAddress.setValue("将军大道100号", Address.Detail);

        Assert.assertTrue(person.getStruct(Person.BothAddress).getValue(Address.Province).equals("江苏省"));
        Assert.assertTrue(person.getStruct(Person.BothAddress).getValue(Address.City).equals("南京市"));
        Assert.assertTrue(person.getStruct(Person.BothAddress).getValue(Address.District).equals("江宁区"));
        Assert.assertTrue(person.getStruct(Person.BothAddress).getValue(Address.Detail).equals("将军大道100号"));
    }

    @Test
    public void test_changeSameValue() {
        AttributeBag bag = new AttributeBag();
        StructDataAccessor person = bag.newEntity("Person");
        
        person.setValue("Rose", Person.Name);
        person.setValue("Rose", Person.Name);

        Assert.assertTrue(bag.getActionCommandCount() == 1);


        bag.saveChanges(c -> {
            Assert.assertTrue(c.getActionType() == ActionType.Add);
            Assert.assertTrue(c.getAttributeData().getStatus() == DataStatus.Temp);
            Assert.assertTrue("Rose".equals(person.getValue(Person.Name)));
        });

        Assert.assertTrue(bag.getActionCommandCount() == 0);
    }

    @Test
    public void test_tempValue() {
        AttributeBag bag = new AttributeBag();
        StructDataAccessor person = bag.newEntity("Person");

        person.setValue("139-0088-9922", Person.CellPhoneNumber);
        person.setValue("186-9900-8765", Person.CellPhoneNumber);
        person.delete(Person.CellPhoneNumber.getPropertyName());

        Assert.assertTrue(bag.getActionCommandCount() == 0);
    }

    @Test
    public void test_structArray() {
        AttributeBag bag = new AttributeBag();
        StructDataAccessor person = bag.newEntity("Person");

        ArrayDataAccessor addressArray = person.newArray("addressList");
        addressArray.newStruct("Address", "");
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

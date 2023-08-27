package com.soonsoft.uranus.core.common.attribute;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.soonsoft.uranus.core.common.attribute.DynamicEntityDefinition.*;
import com.soonsoft.uranus.core.common.attribute.access.ActionCommand;
import com.soonsoft.uranus.core.common.attribute.access.ActionType;
import com.soonsoft.uranus.core.common.attribute.access.ArrayDataAccessor;
import com.soonsoft.uranus.core.common.attribute.access.StructDataAccessor;
import com.soonsoft.uranus.core.common.attribute.data.AttributeData;
import com.soonsoft.uranus.core.common.attribute.data.DataStatus;
import com.soonsoft.uranus.core.common.lang.StringUtils;

public class AttributeBagTest {
    private AttributeBagFactory bagFactory = new AttributeBagFactory();

    @Test
    public void test_EmptyBag() {
        IAttributeBag bag = bagFactory.createBag();
        StructDataAccessor person = bag.getEntityOrNew("Person");
        
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
        IAttributeBag bag = bagFactory.createBag();
        StructDataAccessor person = bag.getEntityOrNew("Person");
        
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
        IAttributeBag bag = bagFactory.createBag();
        StructDataAccessor person = bag.getEntityOrNew("Person");

        person.setValue("139-0088-9922", Person.CellPhoneNumber);
        person.setValue("186-9900-8765", Person.CellPhoneNumber);
        person.delete(Person.CellPhoneNumber.getPropertyName());

        Assert.assertTrue(bag.getActionCommandCount() == 0);
    }

    @Test
    public void test_structArray() {
        IAttributeBag bag = bagFactory.createBag();
        StructDataAccessor person = bag.getEntityOrNew("Person");

        ArrayDataAccessor addressArray = person.newArray("addressList");
        addressArray.newStruct("Address", "0");
    }

    @Test
    public void test_command() {
        IAttributeBag bag = bagFactory.createBag();
        StructDataAccessor person = bag.getEntityOrNew("Person");
        person.setValue("王大锤", Person.Name); // Add
        person.setValue("张国焘", Person.Name); // Modify

        Assert.assertTrue(bag.getActionCommandCount() == 1);
        List<ActionCommand> commands = getCommands(bag);
        Assert.assertTrue(commands.get(0).getActionType() == ActionType.Add);
        Assert.assertTrue(commands.get(0).getAttributeData().getPropertyValue().equals("张国焘"));


        person.delete(Person.Name.getPropertyName()); // Delete
        person.setValue("刘大石", Person.Name); // Add new

        Assert.assertTrue(bag.getActionCommandCount() == 2);
        commands = getCommands(bag);
        Assert.assertTrue(commands.get(0).getActionType() == ActionType.Delete);
        Assert.assertTrue(commands.get(0).getAttributeData().getPropertyValue().equals("张国焘"));
        Assert.assertTrue(commands.get(1).getActionType() == ActionType.Add);
        Assert.assertTrue(commands.get(1).getAttributeData().getPropertyValue().equals("刘大石"));


        person.setValue("晓峰", Person.Name); // Modify
        person.delete(Person.Name.getPropertyName()); // Delete

        Assert.assertTrue(bag.getActionCommandCount() == 1);
        commands = getCommands(bag);
        Assert.assertTrue(commands.get(0).getActionType() == ActionType.Delete);
        Assert.assertTrue(commands.get(0).getAttributeData().getPropertyValue().equals("晓峰"));
    }

    @Test
    public void test_restore() {
        List<AttributeData> data = 
            AttributeDataBuilder.create().entity("Person")
                .property("Name", "Jack")
                .array("CellPhoneNumber", "131-1111-1111", "132-2222-2222", "133-3333-3333")
                .property("Birthday", "1994-03-08")
                .struct("Address", "BothAddress")
                    .property("Province", "浙江")
                    .property("City", "衢州")
                    .property("District", "江山")
                    .property("Detail", "城中路")
                    .done()
                .getData();

        IAttributeBag bag = bagFactory.createBag(data);
        StructDataAccessor person = bag.getEntity("Person");

        Assert.assertTrue(StringUtils.equals(person.getValue(Person.Name), "Jack"));

        ArrayDataAccessor phoneArray = person.getArray(Person.CellPhoneNumber);
        Assert.assertTrue(phoneArray.size() == 3);
        Assert.assertTrue(StringUtils.equals(phoneArray.getValue(0, Person.CellPhoneNumber), "131-1111-1111"));
        Assert.assertTrue(StringUtils.equals(phoneArray.getValue(1, Person.CellPhoneNumber), "132-2222-2222"));
        Assert.assertTrue(StringUtils.equals(phoneArray.getValue(2, Person.CellPhoneNumber), "133-3333-3333"));

        StructDataAccessor address = person.getStruct(Person.BothAddress);
        Assert.assertTrue(StringUtils.equals(address.getValue(Address.Province), "浙江"));
        Assert.assertTrue(StringUtils.equals(address.getValue(Address.City), "衢州"));
        Assert.assertTrue(StringUtils.equals(address.getValue(Address.District), "江山"));
        Assert.assertTrue(StringUtils.equals(address.getValue(Address.Detail), "城中路"));
    }

    private static List<ActionCommand> getCommands(IAttributeBag bag) {
        List<ActionCommand> commands = new ArrayList<>();
        bag.saveChanges(cmd -> {
            commands.add(cmd);
        });
        return commands;
    }

}

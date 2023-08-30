package com.soonsoft.uranus.core.common.attribute;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.soonsoft.uranus.core.common.attribute.DynamicEntityDefinition.Address;
import com.soonsoft.uranus.core.common.attribute.DynamicEntityDefinition.Person;
import com.soonsoft.uranus.core.common.attribute.access.ActionCommand;
import com.soonsoft.uranus.core.common.attribute.access.ArrayDataAccessor;
import com.soonsoft.uranus.core.common.attribute.access.StructDataAccessor;
import com.soonsoft.uranus.core.common.attribute.convertor.AttributeDataType;
import com.soonsoft.uranus.core.common.attribute.data.PropertyType;
import com.soonsoft.uranus.core.common.attribute.notify.Watcher;
import com.soonsoft.uranus.core.common.lang.DateTimeUtils;
import com.soonsoft.uranus.core.common.lang.StringUtils;

public class AttributeBagReactiveTest {
    private AttributeBagFactory bagFactory = new AttributeBagFactory();

    @Test
    public void test_computedProperty() {
        IAttributeBag bag = bagFactory.createBag();
        StructDataAccessor person = bag.getEntityOrNew("Person");
        person.setValue("Tony", Person.Name);
        person.setValue(DateTimeUtils.parseDay("1980-01-29"), Person.Birthday);
        person.createComputedProperty(Person.Age, p -> {
            Date birthday = p.getValue(Person.Birthday);
            if(birthday == null) {
                return null;
            }
            
            int year = Calendar.getInstance().get(Calendar.YEAR);
            Calendar birthdayCalendar = Calendar.getInstance();
            birthdayCalendar.setTime(birthday);
            int birthdayYear = birthdayCalendar.get(Calendar.YEAR);
            return year - birthdayYear;
        });

        Assert.assertTrue(person.getValue(Person.Age).equals(43));

        person.setValue(DateTimeUtils.parseDay("1983-01-01"), Person.Birthday);
        Assert.assertTrue(person.getValue(Person.Age).equals(40));

        List<ActionCommand> commands = new ArrayList<>();
        bag.saveChanges(cmd -> {
            commands.add(cmd);
        });

        Assert.assertTrue(commands.size() == 3);
    }

    @Test
    public void test_structProperty() {
        IAttributeBag bag = bagFactory.createBag();
        StructDataAccessor person = bag.getEntityOrNew("Person");

        StructDataAccessor bothAddress = person.newStruct(Person.BothAddress);
        Attribute<String> bothAddressDetail = new Attribute<>("Person", "BothAddressDetail", PropertyType.ComputedProperty, AttributeDataType.StringConvetor);
        person.createComputedProperty(bothAddressDetail, p -> p.getStruct(Person.BothAddress).getValue(Address.Detail));
        Assert.assertTrue(person.getValue(bothAddressDetail) == null);

        bothAddress.setValue("中国江苏省南京市", Address.Detail);
        Assert.assertTrue(StringUtils.equals(person.getValue(bothAddressDetail), "中国江苏省南京市"));

    }

    @Test
    public void test_arrayProperty() {
        IAttributeBag bag = bagFactory.createBag();
        StructDataAccessor person = bag.getEntityOrNew("Person");

        ArrayDataAccessor array = person.newArray("CellPhoneNumber");
        Attribute<Integer> cellPhoneCount = new Attribute<>("Person", "CellPhoneCount", PropertyType.ComputedProperty, AttributeDataType.IntegerConvetor);
        int[] triggerCount = new int[] { 0 };
        person.createComputedProperty(cellPhoneCount, p -> {
            triggerCount[0]++;
            System.out.println(String.format("trigger CellPhoneCount %d", triggerCount[0]));
            return p.getArray(Person.CellPhoneNumber).size();
        });
        Assert.assertTrue(triggerCount[0] == 1);

        array.addValue("138-0920-8909", Person.CellPhoneNumber);
        Assert.assertTrue(person.getValue(cellPhoneCount).equals(1));
        Assert.assertTrue(triggerCount[0] == 2);

        array.addValue("186-7689-1234", Person.CellPhoneNumber);
        Assert.assertTrue(person.getValue(cellPhoneCount).equals(2));
        Assert.assertTrue(triggerCount[0] == 3);

        array.setValue("198-0099-1188", 0, Person.CellPhoneNumber);
        Assert.assertTrue(triggerCount[0] == 4);
    }

    @Test
    public void test_BagWatch() {
        IAttributeBag bag1 = bagFactory.createBag();
        StructDataAccessor person1 = bag1.getEntityOrNew("Person");
        person1.setValue("小花", Person.Name);
        person1.setValue(DateTimeUtils.parseDay("1987-09-11"), Person.Birthday);

        IAttributeBag bag2 = bagFactory.createBag();
        StructDataAccessor person2 = bag2.getEntityOrNew("Person");
        person2.setValue("小明", Person.Name);
        person2.setValue(DateTimeUtils.parseDay("1982-02-17"), Person.Birthday);

        Watcher<String> watcher = bagFactory.watch(() -> {
            Date birthday1 = person1.getValue(Person.Birthday);
            Date birthday2 = person2.getValue(Person.Birthday);

            String name1 = person1.getValue(Person.Name);
            String name2 = person2.getValue(Person.Name);

            return birthday1.compareTo(birthday2) < 0 ? name1 : name2;
        });

        Assert.assertTrue(watcher.getComputedValue().equals("小明"));

        StructDataAccessor person3 = bag1.getEntityOrNew("Person");
        person3.setValue("小华", Person.Name);
        person3.setValue(DateTimeUtils.parseDay("1975-06-01"), Person.Birthday);

        Assert.assertTrue(watcher.getComputedValue().equals("小华"));
    }
    
}

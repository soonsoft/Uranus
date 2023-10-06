package com.soonsoft.uranus.core.common.attribute;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.soonsoft.uranus.core.common.attribute.DynamicEntityDefinition.Account;
import com.soonsoft.uranus.core.common.attribute.DynamicEntityDefinition.AccountStatus;
import com.soonsoft.uranus.core.common.attribute.DynamicEntityDefinition.BankCard;
import com.soonsoft.uranus.core.common.attribute.DynamicEntityDefinition.CustomerType;
import com.soonsoft.uranus.core.common.attribute.DynamicEntityDefinition.Person;
import com.soonsoft.uranus.core.common.attribute.access.ActionCommand;
import com.soonsoft.uranus.core.common.attribute.access.ActionType;
import com.soonsoft.uranus.core.common.attribute.access.ArrayDataAccessor;
import com.soonsoft.uranus.core.common.attribute.access.StructDataAccessor;
import com.soonsoft.uranus.core.common.attribute.convertor.AttributeDataType;
import com.soonsoft.uranus.core.common.attribute.data.AttributeData;
import com.soonsoft.uranus.core.common.attribute.data.PropertyType;
import com.soonsoft.uranus.core.common.attribute.notify.Watcher;
import com.soonsoft.uranus.core.common.lang.DateTimeUtils;

public class AttributeBagReactiveTest {
    private AttributeBagFactory bagFactory = new AttributeBagFactory();

    @Test
    public void test_computedProperty() {
        IAttributeBag bag = bagFactory.createBag();
        StructDataAccessor person = bag.getEntityOrNew("Person");
        person.setValue("Tony", Person.Name);
        person.setValue(DateTimeUtils.parseDay("1980-01-29"), Person.Birthday);
        person.createComputedProperty(Person.Age);

        Assert.assertTrue(person.getValue(Person.Age).equals(43));

        person.setValue(DateTimeUtils.parseDay("1983-01-01"), Person.Birthday);
        Assert.assertTrue(person.getValue(Person.Age).equals(40));

        List<ActionCommand> commands = new ArrayList<>();
        bag.saveChanges(cmd -> {
            commands.add(cmd);
        });

        Assert.assertTrue(commands.size() == 3);
        Assert.assertTrue(commands.get(0).getActionType() == ActionType.Add);
        Assert.assertTrue(commands.get(0).getAttributeData().getPropertyName().equals(Person.Name.getPropertyName()));

        Assert.assertTrue(commands.get(1).getActionType() == ActionType.Add);
        Assert.assertTrue(commands.get(1).getAttributeData().getPropertyName().equals(Person.Birthday.getPropertyName()));

        Assert.assertTrue(commands.get(2).getActionType() == ActionType.Add);
        Assert.assertTrue(commands.get(2).getAttributeData().getPropertyName().equals(Person.Age.getPropertyName()));
    }

    @Test
    public void test_structProperty() {
        IAttributeBag bag = bagFactory.createBag();
        StructDataAccessor account = bag.getEntityOrNew("Account");

        // 先创建依赖项，再创建计算属性，否则不会收集依赖
        StructDataAccessor bankCard = account.newStruct(Account.BankCard);
        // 定义计算属性
        Attribute<String> BankCardChangedValue = new Attribute<>("Account", "BankCardChangedValue", PropertyType.ComputedProperty, AttributeDataType.String);
        account.createComputedProperty(BankCardChangedValue, p -> {
            String changedValue = null;
            var card = p.getStruct(Account.BankCard);
            if(card != null) {
                changedValue = card.getValue(BankCard.BankCardAccountNo);
            }
            return changedValue;
        });
        Assert.assertTrue(account.getValue(BankCardChangedValue) == null);

        bankCard.setValue("刘大石", BankCard.BankCardAccountName);
        bankCard.setValue("89078222", BankCard.BankCardAccountNo);
        bankCard.setValue("中国银行", BankCard.BankName);
        bankCard.setValue("ICBCHKXXX", BankCard.SwiftCode);
        // 计算属性值更新
        Assert.assertTrue(account.getValue(BankCardChangedValue).equals(bankCard.getValue(BankCard.BankCardAccountNo)));

        // 再次更新
        bankCard.setValue("123456789", BankCard.BankCardAccountNo);
        Assert.assertTrue(account.getValue(BankCardChangedValue).equals("123456789"));
    }

    @Test
    public void test_arrayProperty() {
        IAttributeBag bag = bagFactory.createBag();
        StructDataAccessor person = bag.getEntityOrNew("Person");

        ArrayDataAccessor array = person.newArray("CellPhoneNumber");
        Attribute<Integer> cellPhoneCount = new Attribute<>("Person", "CellPhoneCount", PropertyType.ComputedProperty, AttributeDataType.Integer);
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
        Assert.assertTrue(person.getValue(cellPhoneCount).equals(2));
    }

    @Test
    public void test_resumeComputedProperty() {
        IAttributeBag bag = bagFactory.createBag();
        StructDataAccessor account = bag.getEntityOrNew("Account", "PN0001");

        StructDataAccessor person = account.newStruct(Account.PersonInfo);
        person.setValue("王军", Person.Name);
        person.setValue(DateTimeUtils.parseDay("1982-12-29"), Person.Birthday);
        person.createComputedProperty(Person.Age);

        account.setValue("投资基金账户", Account.AccountName);
        account.setValue(CustomerType.Individual, Account.CustomerType);
        account.setValue(AccountStatus.AccountOpened, Account.AccountStatus);
        account.createComputedProperty(Account.CustomerName);

        List<AttributeData> saveData = new ArrayList<>();
        bag.saveChanges(cmd -> saveData.add(cmd.getAttributeData()));

        Assert.assertTrue(saveData.size() == 8);

        bag = bagFactory.createBag(saveData, attr -> {
            if(attr.getPropertyName().equals(Account.CustomerName.getPropertyName())) {
                return Account.CustomerName;
            }
            if(attr.getPropertyName().equals(Person.Age.getPropertyName())) {
                return Person.Age;
            }

            return null;
        });

        account = bag.getEntity("Account");
        person = account.getStruct(Account.PersonInfo);
        
        Assert.assertTrue(account.getValue(Account.CustomerName).equals("王军"));
        Assert.assertTrue(person.getValue(Person.Age).equals(41));

        person.setValue("Nick", Person.Name);
        person.setValue(DateTimeUtils.parseDay("1987-09-12"), Person.Birthday);

        Assert.assertTrue(account.getValue(Account.CustomerName).equals("Nick"));
        Assert.assertTrue(person.getValue(Person.Age).equals(36));
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

package com.soonsoft.uranus.core.common.attribute;

import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;

import com.soonsoft.uranus.core.common.attribute.access.StructDataAccessor;
import com.soonsoft.uranus.core.common.attribute.convertor.AttributeDataType;
import com.soonsoft.uranus.core.common.attribute.convertor.IAttributeConvertor;
import com.soonsoft.uranus.core.common.attribute.data.PropertyType;

public interface DynamicEntityDefinition {

    static abstract class Person {
        private static <TValue> Attribute<TValue> define(String propertyName, IAttributeConvertor<TValue> convertor) {
            return new Attribute<>("Person", propertyName, convertor);
        }

        public final static Attribute<String> Name = define("Name", AttributeDataType.String);
        public final static Attribute<Date> Birthday = define("Birthday", AttributeDataType.DateTime);
        public final static Attribute<String> CellPhoneNumber = define("CellPhoneNumber", AttributeDataType.String);
        public final static Attribute<Object> BothAddress = new Attribute<Object>("BothAddress", "BothAddress", PropertyType.Struct);
        public final static ComputedAttribute<Integer> Age = 
            new ComputedAttribute<>(
                "Person", "Age", AttributeDataType.Integer, 
                p -> {
                    Date birthday = p.getValue(Person.Birthday);
                    if(birthday == null) {
                        return null;
                    }

                    
                    Calendar birthdayCalendar = Calendar.getInstance();
                    birthdayCalendar.setTime(birthday);

                     LocalDate dateNow = LocalDate.now();
                    LocalDate dateBirth = LocalDate.of(birthdayCalendar.get(Calendar.YEAR), birthdayCalendar.get(Calendar.MONTH) + 1, birthdayCalendar.get(Calendar.DAY_OF_MONTH));

                    Period period = Period.between(dateBirth, dateNow);
                    return period.getYears();
                }
            );
    }

    static abstract class Address {
        private static <TValue> Attribute<TValue> define(String propertyName, IAttributeConvertor<TValue> convertor) {
            return new Attribute<>("Address", propertyName, convertor);
        }
        public final static Attribute<String> Province = define("Province", AttributeDataType.String);
        public final static Attribute<String> City = define("City", AttributeDataType.String);
        public final static Attribute<String> District = define("District", AttributeDataType.String);
        public final static Attribute<String> Detail = define("Detail", AttributeDataType.String);
    }

    static abstract class Account {
        private static <TValue> Attribute<TValue> define(String propertyName, IAttributeConvertor<TValue> convertor) {
            return new Attribute<>("Account", propertyName, convertor);
        }

        public final static Attribute<String> AccountName = define("AccountName", AttributeDataType.String);
        public final static Attribute<CustomerType> CustomerType = define("CustomerType", AttributeDataType.createEnumConvertor(CustomerType.class));
        public final static ComputedAttribute<String> CustomerName = 
            new ComputedAttribute<>(
                "Account", "CustomerName", AttributeDataType.String, 
                p -> {
                    StructDataAccessor personInfo = p.getStruct(Account.PersonInfo);
                    if(personInfo != null) {
                        return personInfo.getValue(Person.Name);
                    }
                    return null;
                }
            );
        public final static Attribute<AccountStatus> AccountStatus = define("AccountStatus", AttributeDataType.createEnumConvertor(AccountStatus.class));
        public final static Attribute<Object> PersonInfo = new Attribute<>("Person", "PersonInfo", PropertyType.Struct);
        public final static Attribute<Object> BankCard = new Attribute<>("BankCard", "BankCard", PropertyType.Struct);
    }

    static abstract class BankCard {
        private static <TValue> Attribute<TValue> define(String propertyName, IAttributeConvertor<TValue> convertor) {
            return new Attribute<>("BankCard", propertyName, convertor);
        }

        public final static Attribute<String> BankCardAccountName = define("BankCardAccountName", AttributeDataType.String);
        public final static Attribute<String> BankCardAccountNo = define("BankCardAccountNo", AttributeDataType.String);
        public final static Attribute<String> BankName = define("BankName", AttributeDataType.String);
        public final static Attribute<String> SwiftCode = define("SwiftCode", AttributeDataType.String);
    }

    static enum CustomerType {
        Individual,
        Organization,
        ;
    }

    static enum AccountStatus {
        Draft,
        CheckingForKYC,
        CheckingForCompliance,
        CheckingForRO,
        AccountOpened,
        ;
    }

    static enum CertificateType {
        ID,
        Passport,
        ResidentCard,
        ;
    }
    
}

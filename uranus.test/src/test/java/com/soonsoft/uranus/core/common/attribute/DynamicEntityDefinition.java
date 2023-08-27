package com.soonsoft.uranus.core.common.attribute;

import java.util.Date;

import com.soonsoft.uranus.core.common.attribute.convertor.AttributeDataType;
import com.soonsoft.uranus.core.common.attribute.convertor.IAttributeConvertor;
import com.soonsoft.uranus.core.common.attribute.data.PropertyType;

public interface DynamicEntityDefinition {

    static abstract class Person {
        private static <TValue> Attribute<TValue> define(String propertyName, IAttributeConvertor<TValue> convertor) {
            return new Attribute<>("Person", propertyName, convertor);
        }
        public final static Attribute<String> Name = define("Name", AttributeDataType.StringConvetor);
        public final static Attribute<Date> Birthday = define("Birthday", AttributeDataType.DateTimeonvetor);
        public final static Attribute<String> CellPhoneNumber = define("CellPhoneNumber", AttributeDataType.StringConvetor);
        public final static Attribute<Object> BothAddress = new Attribute<Object>("BothAddress", "BothAddress", PropertyType.Struct);
        public final static Attribute<Integer> Age = new Attribute<>("Person", "Age", PropertyType.ComputedProperty, AttributeDataType.IntegerConvetor);
    }

    static abstract class Address {
        private static <TValue> Attribute<TValue> define(String propertyName, IAttributeConvertor<TValue> convertor) {
            return new Attribute<>("Address", propertyName, convertor);
        }
        public final static Attribute<String> Province = define("Province", AttributeDataType.StringConvetor);
        public final static Attribute<String> City = define("City", AttributeDataType.StringConvetor);
        public final static Attribute<String> District = define("District", AttributeDataType.StringConvetor);
        public final static Attribute<String> Detail = define("Detail", AttributeDataType.StringConvetor);
    }
    
}

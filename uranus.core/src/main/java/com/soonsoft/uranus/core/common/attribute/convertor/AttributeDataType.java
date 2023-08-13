package com.soonsoft.uranus.core.common.attribute.convertor;

import java.math.BigDecimal;
import java.util.Date;

import com.soonsoft.uranus.core.common.lang.DateTimeUtils;
import com.soonsoft.uranus.core.common.lang.StringUtils;

public interface AttributeDataType {

    IAttributeConvertor<Boolean> BooleanConvertor = new IAttributeConvertor<Boolean>() {
        @Override
        public Boolean convert(String attributeValue, String defaultValue) {
            String strVal = StringUtils.isEmpty(attributeValue) ? defaultValue : attributeValue;
            return StringUtils.isEmpty(strVal) ? null : Boolean.valueOf(strVal);
        }
    };

    IAttributeConvertor<Byte> ByteConvertor = new IAttributeConvertor<Byte>() {
        @Override
        public Byte convert(String attributeValue, String defaultValue) {
            String strVal = StringUtils.isEmpty(attributeValue) ? defaultValue : attributeValue;
            return StringUtils.isEmpty(strVal) ? null : Byte.valueOf(strVal);
        }
    };

    IAttributeConvertor<Short> ShortConvertor = new IAttributeConvertor<Short>() {
        @Override
        public Short convert(String attributeValue, String defaultValue) {
            String strVal = StringUtils.isEmpty(attributeValue) ? defaultValue : attributeValue;
            return StringUtils.isEmpty(strVal) ? null : Short.valueOf(strVal);
        }
    };

    IAttributeConvertor<Integer> IntegerConvetor = new IAttributeConvertor<Integer>() {
        @Override
        public Integer convert(String attributeValue, String defaultValue) {
            String strVal = StringUtils.isEmpty(attributeValue) ? defaultValue : attributeValue;
            return StringUtils.isEmpty(strVal) ? null : Integer.valueOf(strVal);
        }
    };

    IAttributeConvertor<Long> LongConvetor = new IAttributeConvertor<Long>() {
        @Override
        public Long convert(String attributeValue, String defaultValue) {
            String strVal = StringUtils.isEmpty(attributeValue) ? defaultValue : attributeValue;
            return StringUtils.isEmpty(strVal) ? null : Long.valueOf(strVal);
        }
    };

    IAttributeConvertor<Float> FloatConvetor = new IAttributeConvertor<Float>() {
        @Override
        public Float convert(String attributeValue, String defaultValue) {
            String strVal = StringUtils.isEmpty(attributeValue) ? defaultValue : attributeValue;
            return StringUtils.isEmpty(strVal) ? null : Float.valueOf(strVal);
        }
    };

    IAttributeConvertor<Double> DoubleConvetor = new IAttributeConvertor<Double>() {
        @Override
        public Double convert(String attributeValue, String defaultValue) {
            String strVal = StringUtils.isEmpty(attributeValue) ? defaultValue : attributeValue;
            return StringUtils.isEmpty(strVal) ? null : Double.valueOf(strVal);
        }
    };

    IAttributeConvertor<BigDecimal> BigDecimalConvetor = new IAttributeConvertor<BigDecimal>() {
        @Override
        public BigDecimal convert(String attributeValue, String defaultValue) {
            String strVal = StringUtils.isEmpty(attributeValue) ? defaultValue : attributeValue;
            return StringUtils.isEmpty(strVal) ? null : new BigDecimal(strVal);
        }
    };
    
    IAttributeConvertor<String> StringConvetor = new IAttributeConvertor<String>() {
        @Override
        public String convert(String attributeValue, String defaultValue) {
            return StringUtils.isEmpty(attributeValue) ? defaultValue : attributeValue;
        }
    };

    IAttributeConvertor<Date> DateTimeonvetor = new IAttributeConvertor<Date>() {
        @Override
        public Date convert(String attributeValue, String defaultValue) {
            String strVal = StringUtils.isEmpty(attributeValue) ? defaultValue : attributeValue;
            return StringUtils.isEmpty(strVal) ? null : DateTimeUtils.parse(strVal);
        }
    };

}

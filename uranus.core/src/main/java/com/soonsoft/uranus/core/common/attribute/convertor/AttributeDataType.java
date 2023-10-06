package com.soonsoft.uranus.core.common.attribute.convertor;

import java.math.BigDecimal;
import java.util.Date;

import com.soonsoft.uranus.core.common.lang.DateTimeUtils;
import com.soonsoft.uranus.core.common.lang.StringUtils;

public interface AttributeDataType {

    IAttributeConvertor<Boolean> Boolean = new IAttributeConvertor<Boolean>() {
        @Override
        public Boolean convert(String attributeValue, String defaultValue) {
            String strVal = StringUtils.isEmpty(attributeValue) ? defaultValue : attributeValue;
            return StringUtils.isEmpty(strVal) ? null : java.lang.Boolean.valueOf(strVal);
        }
    };

    IAttributeConvertor<Byte> Byte = new IAttributeConvertor<Byte>() {
        @Override
        public Byte convert(String attributeValue, String defaultValue) {
            String strVal = StringUtils.isEmpty(attributeValue) ? defaultValue : attributeValue;
            return StringUtils.isEmpty(strVal) ? null : java.lang.Byte.valueOf(strVal);
        }
    };

    IAttributeConvertor<Short> Short = new IAttributeConvertor<Short>() {
        @Override
        public Short convert(String attributeValue, String defaultValue) {
            String strVal = StringUtils.isEmpty(attributeValue) ? defaultValue : attributeValue;
            return StringUtils.isEmpty(strVal) ? null : java.lang.Short.valueOf(strVal);
        }
    };

    IAttributeConvertor<Integer> Integer = new IAttributeConvertor<Integer>() {
        @Override
        public Integer convert(String attributeValue, String defaultValue) {
            String strVal = StringUtils.isEmpty(attributeValue) ? defaultValue : attributeValue;
            return StringUtils.isEmpty(strVal) ? null : java.lang.Integer.valueOf(strVal);
        }
    };

    IAttributeConvertor<Long> Long = new IAttributeConvertor<Long>() {
        @Override
        public Long convert(String attributeValue, String defaultValue) {
            String strVal = StringUtils.isEmpty(attributeValue) ? defaultValue : attributeValue;
            return StringUtils.isEmpty(strVal) ? null : java.lang.Long.valueOf(strVal);
        }
    };

    IAttributeConvertor<Float> Float = new IAttributeConvertor<Float>() {
        @Override
        public Float convert(String attributeValue, String defaultValue) {
            String strVal = StringUtils.isEmpty(attributeValue) ? defaultValue : attributeValue;
            return StringUtils.isEmpty(strVal) ? null : java.lang.Float.valueOf(strVal);
        }
    };

    IAttributeConvertor<Double> Double = new IAttributeConvertor<Double>() {
        @Override
        public Double convert(String attributeValue, String defaultValue) {
            String strVal = StringUtils.isEmpty(attributeValue) ? defaultValue : attributeValue;
            return StringUtils.isEmpty(strVal) ? null : java.lang.Double.valueOf(strVal);
        }
    };

    IAttributeConvertor<BigDecimal> BigDecimal = new IAttributeConvertor<BigDecimal>() {
        @Override
        public BigDecimal convert(String attributeValue, String defaultValue) {
            String strVal = StringUtils.isEmpty(attributeValue) ? defaultValue : attributeValue;
            return StringUtils.isEmpty(strVal) ? null : new BigDecimal(strVal);
        }
    };
    
    IAttributeConvertor<String> String = new IAttributeConvertor<String>() {
        @Override
        public String convert(String attributeValue, String defaultValue) {
            return StringUtils.isEmpty(attributeValue) ? defaultValue : attributeValue;
        }
    };

    IAttributeConvertor<Date> DateTime = new IAttributeConvertor<Date>() {
        @Override
        public Date convert(String attributeValue, String defaultValue) {
            String strVal = StringUtils.isEmpty(attributeValue) ? defaultValue : attributeValue;
            return StringUtils.isEmpty(strVal) ? null : DateTimeUtils.parse(strVal);
        }

        @Override
        public String toStringValue(Date value) {
            return value == null ? null : DateTimeUtils.format(value);
        }
    };

    public static <TEnum extends Enum<TEnum>> IAttributeConvertor<TEnum> createEnumConvertor(Class<TEnum> enumClass) {
        return new IAttributeConvertor<TEnum>() {
            @Override
            public TEnum convert(String attributeValue, String defaultValue) {
                String strVal = StringUtils.isEmpty(attributeValue) ? defaultValue : attributeValue;
                return StringUtils.isEmpty(strVal) ? null : Enum.valueOf(enumClass, strVal);
            }

            @Override
            public String toStringValue(TEnum value) {
                return value == null ? null : value.name();
            }
        };
    }

}

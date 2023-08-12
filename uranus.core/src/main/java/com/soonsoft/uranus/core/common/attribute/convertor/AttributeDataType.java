package com.soonsoft.uranus.core.common.attribute.convertor;

import com.soonsoft.uranus.core.common.lang.StringUtils;

public interface AttributeDataType {

    IAttributeConvertor<String> StringConvetor = new IAttributeConvertor<String>() {
        @Override
        public String convert(String attributeValue, String defaultValue) {
            return StringUtils.isEmpty(attributeValue) ? defaultValue : attributeValue;
        }
    };

    IAttributeConvertor<Integer> IntegerConvetor = new IAttributeConvertor<Integer>() {
        @Override
        public Integer convert(String attributeValue, String defaultValue) {
            String strVal = StringUtils.isEmpty(attributeValue) ? defaultValue : attributeValue;
            return StringUtils.isEmpty(strVal) ? null : Integer.valueOf(strVal);
        }
    };

    IAttributeConvertor<Boolean> BooleanConvertor = new IAttributeConvertor<Boolean>() {
        @Override
        public Boolean convert(String attributeValue, String defaultValue) {
            String strVal = StringUtils.isEmpty(attributeValue) ? defaultValue : attributeValue;
            return StringUtils.isEmpty(strVal) ? null : Boolean.valueOf(strVal);
        }
    };

}

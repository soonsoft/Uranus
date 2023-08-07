package com.soonsoft.uranus.core.common.attribute.convertor;

import com.soonsoft.uranus.core.common.lang.StringUtils;

public class IntegerConvertor implements IAttributeConvertor<Integer> {

    @Override
    public Integer convert(String attributeValue, String defaultValue) {
        String strVal = StringUtils.isEmpty(attributeValue) ? defaultValue : attributeValue;
        return StringUtils.isEmpty(strVal) ? null : Integer.valueOf(strVal);
    }
    
}

package com.soonsoft.uranus.core.common.attribute.convertor;

import com.soonsoft.uranus.core.common.lang.StringUtils;

public class BooleanConvertor implements IAttributeConvertor<Boolean> {

    @Override
    public Boolean convert(String attributeValue, String defaultValue) {
        String strVal = StringUtils.isEmpty(attributeValue) ? defaultValue : attributeValue;
        return StringUtils.isEmpty(strVal) ? null : Boolean.valueOf(strVal);
    }
    
}

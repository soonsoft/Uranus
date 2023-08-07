package com.soonsoft.uranus.core.common.attribute.convertor;

import com.soonsoft.uranus.core.common.lang.StringUtils;

public class StringConvertor implements IAttributeConvertor<String> {

    @Override
    public String convert(String attributeValue, String defaultValue) {
        return StringUtils.isEmpty(attributeValue) ? defaultValue : attributeValue;
    }
    
}

package com.soonsoft.uranus.core.common.attribute;

public interface IAttributeJsonTemplate {
    default String getJSON() {
        return getJSON(null);
    }

    String getJSON(String entityName);
}

package com.soonsoft.uranus.core.common.attribute.data;

import java.util.UUID;

public class AttributeKey {
    public String generate() {
        return UUID.randomUUID().toString();
    }
}

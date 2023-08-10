package com.soonsoft.uranus.core.common.attribute;

public class AttributeException extends RuntimeException {
    public AttributeException(String message) {
        super(message);
    }

    public AttributeException(String message, Object... params) {
        super(String.format(message, params));
    }

    public AttributeException(String message, Throwable e) {
        super(message, e);
    }
}

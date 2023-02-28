package com.soonsoft.uranus.core.error.argument;

public class ArgumentNullException extends ArgumentException {

    private static final String MESSAGE_TEMPLATE = "the parameter [%s] is required.";

    public ArgumentNullException(String argumentName) {
        super(MESSAGE_TEMPLATE, argumentName);
    }

    public ArgumentNullException(String argumentName, Throwable e) {
        super(e, MESSAGE_TEMPLATE, argumentName);
    }
    
}

package com.soonsoft.uranus.core.error.argument;

public class ArgumentNullException extends IllegalArgumentException {

    public ArgumentNullException(String argumentName) {
        this(argumentName, null);
    }

    public ArgumentNullException(String argumentName, Throwable e) {
        super(String.format("the parameter [%s] is required.", argumentName), e);
    }
    
}

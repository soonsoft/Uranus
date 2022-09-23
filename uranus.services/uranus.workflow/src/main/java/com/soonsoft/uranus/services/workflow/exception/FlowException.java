package com.soonsoft.uranus.services.workflow.exception;

public class FlowException extends IllegalStateException {

    public FlowException(String message) {
        super(message);
    }

    public FlowException(String messageFormat, Object... params) {
        super(String.format(messageFormat, params));
    }

    public FlowException(String message, Throwable e) {
        super(message, e);
    }
    
}

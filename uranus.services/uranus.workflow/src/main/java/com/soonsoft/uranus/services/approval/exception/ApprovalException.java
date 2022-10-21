package com.soonsoft.uranus.services.approval.exception;

public class ApprovalException extends IllegalStateException {
    
    public ApprovalException(String message) {
        super(message);
    }

    public ApprovalException(String messageFormat, Object... params) {
        super(String.format(messageFormat, params));
    }

    public ApprovalException(String message, Throwable e) {
        super(message, e);
    }

}

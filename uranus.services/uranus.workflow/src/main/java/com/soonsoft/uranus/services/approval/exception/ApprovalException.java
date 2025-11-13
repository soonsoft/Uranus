package com.soonsoft.uranus.services.approval.exception;

import com.soonsoft.uranus.core.error.ApplicationRuntimeException;

public class ApprovalException extends ApplicationRuntimeException {
    
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

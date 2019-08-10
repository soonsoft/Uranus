package com.soonsoft.uranus.web.error;

import com.soonsoft.uranus.core.error.BusinessException;

/**
 * Http Action处理异常描述
 */
public class HttpActionException extends BusinessException {

    private static final long serialVersionUID = 1L;

    private Object attachment;

    public HttpActionException(String message) {
        this(message, null, null);
    }

    public HttpActionException(String message, Throwable e) {
        this(message, e, null);
    }

    public HttpActionException(String message, Throwable e, Object attachment) {
        super(message, e);
        this.attachment = attachment;
    }

    public Object getAttachment() {
        return attachment;
    }
    
}
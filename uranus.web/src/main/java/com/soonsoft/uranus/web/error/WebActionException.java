package com.soonsoft.uranus.web.error;

import com.soonsoft.uranus.core.error.BusinessException;

/**
 * Http Action处理异常描述
 */
public class WebActionException extends BusinessException {

    private static final long serialVersionUID = 1L;

    private Object attachment;

    public WebActionException(String message) {
        this(message, null, null);
    }

    public WebActionException(String message, Throwable e) {
        this(message, e, null);
    }

    public WebActionException(String message, Throwable e, Object attachment) {
        super(message, e);
        this.attachment = attachment;
    }

    public Object getAttachment() {
        return attachment;
    }
    
}
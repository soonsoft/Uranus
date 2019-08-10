package com.soonsoft.uranus.core.error;

/**
 * 业务异常，RuntimeException
 * 可以不用处理，一般作为参数校验等业务异常的基类
 * 该类以及该类的子类提供的message信息可以用于用户界面展示
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = -4233148563740555390L;

    public BusinessException(String message) {
        this(message, null);
    }

    public BusinessException(String message, Throwable e) {
        super(message, e);
    }

}
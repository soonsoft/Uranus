package com.soonsoft.uranus.data.config.exception;

public class UranusMybatisConfigurationException extends IllegalStateException {

    private static final long serialVersionUID = 8539068178048786014L;

    private static final String DEFAULT_MESSAGE = "Mybatis configuration error.";

    public UranusMybatisConfigurationException(Throwable e) {
        super(DEFAULT_MESSAGE, e);
    }
    
}

package com.soonsoft.uranus.core.common.extension;

/**
 * ExtensionClassLoadException
 */
public class ExtensionClassLoadException extends RuntimeException {

    private static final long serialVersionUID = -7941272659551671516L;

    public ExtensionClassLoadException(String message) {
        super(message);
    }

    public ExtensionClassLoadException(String message, Throwable e) {
        super(message, e);
    }

}
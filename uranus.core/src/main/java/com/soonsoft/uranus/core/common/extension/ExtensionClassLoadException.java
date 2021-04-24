package com.soonsoft.uranus.core.common.extension;

/**
 * ExtensionClassLoadException
 */
public class ExtensionClassLoadException extends RuntimeException {

    public ExtensionClassLoadException(String message) {
        super(message);
    }

    public ExtensionClassLoadException(String message, Throwable e) {
        super(message, e);
    }

}
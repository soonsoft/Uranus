package com.soonsoft.uranus.core.common.extension;

/**
 * ExtensionFactory
 */
@SPI
public interface ExtensionFactory {

    <T> T getExtension(Class<T> type, String name);
    
}
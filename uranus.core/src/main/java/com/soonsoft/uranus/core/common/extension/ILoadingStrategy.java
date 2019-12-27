package com.soonsoft.uranus.core.common.extension;

/**
 * LoadingStrategy
 */
public interface ILoadingStrategy {

    String directory();

    default boolean preferExtensionClassLoader() {
        return false;
    }

    default String[] excludedPackages() {
        return null;
    } 
    
}
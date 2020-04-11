package com.soonsoft.uranus.util.identity;

import com.soonsoft.uranus.core.common.extension.SPI;

/**
 * IdentityGenerator
 */
@SPI
public interface IdentityGenerator<T> {

    T newID();
    
}
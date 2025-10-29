package com.soonsoft.uranus.core.utils.identity;

import com.soonsoft.uranus.core.common.extension.SPI;

/**
 * IdentityGenerator
 */
@SPI
public interface IdentityGenerator<T> {

    T newID();
    
}
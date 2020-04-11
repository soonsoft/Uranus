package com.soonsoft.uranus.util.identity;

import com.soonsoft.uranus.util.identity.flake.SnowFlakeGenerator;

/**
 * ID
 */
public class ID {

    private static SnowFlakeGenerator generator = new SnowFlakeGenerator();

    static {
        generator.initialize(1);
    }

    public static long newID() {
        return generator.newID();
    }

    public static SnowFlakeGenerator getGenerator() {
        return generator;
    }
    
}


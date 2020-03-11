package com.soonsoft.uranus.util.identity;

/**
 * ID
 */
public class ID {

    private static SnowFlakeGenerator generator = new SnowFlakeGenerator();

    public static long newID() {
        return generator.newID();
    }
    
}


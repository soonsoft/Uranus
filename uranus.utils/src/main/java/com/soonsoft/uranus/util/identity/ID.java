package com.soonsoft.uranus.util.identity;

import com.soonsoft.uranus.util.identity.flake.SnowFlakeGenerator;
import com.soonsoft.uranus.util.identity.guid.GuidGenerator;

public class ID {

    private static SnowFlakeGenerator snowFlakeGenerator = new SnowFlakeGenerator();
    private static GuidGenerator guidGenerator = new GuidGenerator();

    static {
        snowFlakeGenerator.initialize(1);
    }

    public static String newGuid() {
        return guidGenerator.newID();
    }

    public static long newID() {
        return snowFlakeGenerator.newID();
    }

    public static SnowFlakeGenerator getGenerator() {
        return snowFlakeGenerator;
    }
    
}


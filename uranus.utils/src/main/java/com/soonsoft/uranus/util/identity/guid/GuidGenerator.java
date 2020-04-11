package com.soonsoft.uranus.util.identity.guid;

import java.util.UUID;

import com.soonsoft.uranus.util.identity.IdentityGenerator;

/**
 * GuidGenerator
 */
public class GuidGenerator implements IdentityGenerator<String> {

    @Override
    public String newID() {
        return UUID.randomUUID().toString();
    }
    
}